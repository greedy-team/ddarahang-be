#!/bin/bash

BLUE_PORT=8080
GREEN_PORT=8081

timestamp=$(date +"%Y%m%d%H%M%S")

get_current_port() {
    if sudo fuser $BLUE_PORT/tcp >/dev/null 2>&1 && ! sudo fuser $GREEN_PORT/tcp >/dev/null 2>&1; then
        echo "BLUE_PORT: $BLUE_PORT"
    elif sudo fuser $GREEN_PORT/tcp >/dev/null 2>&1 && ! sudo fuser $BLUE_PORT/tcp >/dev/null 2>&1; then
        echo "GREEN_PORT: $GREEN_PORT"
    else
        echo "none"
    fi
}

health_check() {
    local check_url="http://localhost:$1/actuator/health"
    local retry_count=0
    local max_retry=10

    echo "$1 포트에서 헬스 체크 중..."

    while [ $retry_count -lt $max_retry ]; do
        if curl --silent --head "$check_url" >/dev/null 2>&1; then
            echo "$1 포트에서 헬스 체크 통과"
            return 0
        fi
        echo "시도 $((retry_count+1))/$max_retry: 헬스 체크가 아직 통과되지 않았습니다"
        sleep 5
        retry_count=$((retry_count+1))
    done

    echo "$1 포트에서 헬스 체크 실패 ($max_retry회 시도 후)"
    return 1
}

rollback() {
    local port=$1
    local backup_jar="/home/ubuntu/ddarahang/old_build/app-${port}-${timestamp}.jar"
    local current_version_jar="/home/ubuntu/ddarahang/app-${port}.jar"

    echo "$port 포트에서 롤백 중..."

    sudo fuser -k -TERM $port/tcp
    if [ -f "$backup_jar" ]; then
        mv "$backup_jar" "$current_version_jar"
        sudo nohup java -jar \
          -DRDS_HOST="$RDS_HOST" \
          -DRDS_USERNAME="$RDS_USERNAME" \
          -DRDS_PASSWORD="$RDS_PASSWORD" \
          -DGOOGLE_SPREADSHEET_ID="$GOOGLE_SPREADSHEET_ID" \
          -DGOOGLE_SERVICE_ACCOUNT_PATH="$GOOGLE_SERVICE_ACCOUNT_PATH" \
          -Dserver.port="$port" "$current_version_jar" > "/home/ubuntu/console-$port.log" 2>&1 &
    else
        echo "$port 포트에 대한 백업 JAR 파일이 존재하지 않습니다. 롤백을 할 수 없습니다."
        return 1
    fi
}

deploy() {
    local port=$1
    local app_dir="/home/ubuntu/ddarahang"
    local current_version_jar="$app_dir/app-${port}.jar"
    local new_version_jar="$app_dir/build/libs/ddarahang.jar"
    local backup_dir="$app_dir/old_build"

    echo "$port 포트에 새 버전 배포 중..."

    if [ ! -f "$new_version_jar" ]; then
        echo "오류: $new_version_jar 파일을 찾을 수 없습니다. 배포를 중단합니다..."
        exit 1
    fi

    mkdir -p "$backup_dir"
    [ -f "$current_version_jar" ] && mv "$current_version_jar" "$backup_dir/app-${port}-${timestamp}.jar"
    cp "$new_version_jar" "$current_version_jar"
    sudo chmod +x "$current_version_jar"
    sudo nohup java -jar \
      -DRDS_HOST="$RDS_HOST" \
      -DRDS_USERNAME="$RDS_USERNAME" \
      -DRDS_PASSWORD="$RDS_PASSWORD" \
      -DGOOGLE_SPREADSHEET_ID="$GOOGLE_SPREADSHEET_ID" \
      -DGOOGLE_SERVICE_ACCOUNT_PATH="$GOOGLE_SERVICE_ACCOUNT_PATH" \
      -Dserver.port="$port" "$current_version_jar" > "$app_dir/console-$port.log" 2>&1 &

    sleep 20
    if ! health_check "$port"; then
            rollback "$port"
            exit 1
        fi

    echo "$port 포트에 대한 배포 성공"
}

switch_traffic() {
    local config_file="/etc/nginx/sites-available/default"
    local new_proxy_pass="proxy_pass http://127.0.0.1:$2;"

    echo "Nginx 트래픽을 $2 포트로 전환합니다..."
    cp "$config_file" "$config_file.bak"

    sudo sed -i '/location \/ {/!b;n;s|proxy_pass http://127.0.0.1:.*;|'"$new_proxy_pass"'|' "$config_file"
    if [ $? -ne 0 ]; then
        echo "오류: Nginx 설정 업데이트 실패"
        sudo mv "$config_file.bak" "$config_file"
        exit 1
    fi
}

current_port=$(get_current_port)
if [ "$current_port" != "none" ]; then
    if [ "$current_port" -eq "$BLUE_PORT" ]; then
        echo "$BLUE_PORT 포트가 실행 중, $GREEN_PORT 포트로 배포 중..."
        deploy "$GREEN_PORT" || exit 1
        switch_traffic "$BLUE_PORT" "$GREEN_PORT"
        sudo fuser -k -TERM "$BLUE_PORT"/tcp
        echo "Blue-Green 배포 완료 : $GREEN_PORT 포트에서만 실행 중"
    else
        echo "$GREEN_PORT 포트가 실행 중, $BLUE_PORT 포트로 배포 중..."
        deploy "$BLUE_PORT" || exit 1
        switch_traffic "$GREEN_PORT" "$BLUE_PORT"
        sudo fuser -k -TERM "$GREEN_PORT"/tcp
        echo "Blue-Green 배포 완료 : $BLUE_PORT 포트에서만 실행 중"
    fi
else
    echo "실행 중인 포트가 없거나 두 포트 모두 활성화 되어 있습니다. $BLUE_PORT 포트에서 배포 시작..."
    deploy "$BLUE_PORT" || exit 1
    switch_traffic "$GREEN_PORT" "$BLUE_PORT"
    echo "Blue-Green 배포 완료 : $BLUE_PORT 포트에서만 실행 중"
fi

sudo rm /home/ubuntu/ddarahang/build/libs/ddarahang.jar
