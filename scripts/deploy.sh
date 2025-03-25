#!/bin/bash

BLUE_PORT=8080
GREEN_PORT=8081
TIMESTAMP=$(date +"%Y%m%d%H%M%S")

get_current_port() {
    if sudo fuser "$BLUE_PORT/tcp" >/dev/null 2>&1 && ! sudo fuser "$GREEN_PORT/tcp" >/dev/null 2>&1; then
        echo "$BLUE_PORT"
    elif sudo fuser "$GREEN_PORT/tcp" >/dev/null 2>&1 && ! sudo fuser "$BLUE_PORT/tcp" >/dev/null 2>&1; then
        echo "$GREEN_PORT"
    else
        echo "none"
    fi
}

health_check() {
    local check_url="http://localhost:$1/actuator/health"
    local retry_count=0
    local max_retry=10

    while [ "$retry_count" -lt "$max_retry" ]; do
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
    local backup_jar="/home/ubuntu/ddarahang/old_build/app-${port}-${TIMESTAMP}.jar"
    local current_jar="/home/ubuntu/ddarahang/app-${port}.jar"

    sudo fuser -k -TERM "$port/tcp" >/dev/null 2>&1
    if [ -f "$backup_jar" ]; then
        mv "$backup_jar" "$current_jar"
        sudo nohup java -jar \
            -DRDS_HOST="$RDS_HOST" \
            -DRDS_USERNAME="$RDS_USERNAME" \
            -DRDS_PASSWORD="$RDS_PASSWORD" \
            -DGOOGLE_SPREADSHEET_ID="$GOOGLE_SPREADSHEET_ID" \
            -DGOOGLE_SERVICE_ACCOUNT_PATH="$GOOGLE_SERVICE_ACCOUNT_PATH" \
            -Dserver.port="$port" "$current_jar" > "/home/ubuntu/console-$port.log" 2>&1 &
    else
        echo "오류: $port 포트에 대한 백업 JAR 파일이 없습니다. 롤백 실패"
        return 1
    fi
}

deploy() {
    local port=$1
    local app_dir="/home/ubuntu/ddarahang"
    local current_jar="$app_dir/app-${port}.jar"
    local new_jar="$app_dir/build/libs/ddarahang.jar"
    local backup_dir="$app_dir/old_build"

    if [ ! -f "$new_jar" ]; then
        echo "오류: $new_jar 파일을 찾을 수 없습니다. 배포 중단..."
        exit 1
    fi

    mkdir -p "$backup_dir"
    [ -f "$current_jar" ] && mv "$current_jar" "$backup_dir/app-${port}-${TIMESTAMP}.jar"
    cp "$new_jar" "$current_jar"
    sudo chmod +x "$current_jar"
    sudo nohup java -jar \
        -DRDS_HOST="$RDS_HOST" \
        -DRDS_USERNAME="$RDS_USERNAME" \
        -DRDS_PASSWORD="$RDS_PASSWORD" \
        -DGOOGLE_SPREADSHEET_ID="$GOOGLE_SPREADSHEET_ID" \
        -DGOOGLE_SERVICE_ACCOUNT_PATH="$GOOGLE_SERVICE_ACCOUNT_PATH" \
        -Dserver.port="$port" "$current_jar" > "$app_dir/console-$port.log" 2>&1 &

    sleep 20
    if ! health_check "$port"; then
        rollback "$port"
        exit 1
    fi
    echo "$port 포트에 대한 배포 성공"
}

switch_traffic() {
    local config_file="/etc/nginx/sites-available/default"
    local new_proxy_pass="proxy_pass http://127.0.0.1:$1;"

    sudo cp "$config_file" "$config_file.bak"
    sudo sed -i '/location \/ {/!b;n;s|proxy_pass http://127.0.0.1:.*;|'"$new_proxy_pass"'|' "$config_file"

    if sudo nginx -t >/dev/null 2>&1; then
        sudo service nginx reload
        if [ $? -eq 0 ]; then
            echo "Nginx 트래픽 전환 성공!"
        else
            echo "오류: Nginx 재로드 실패, 백업 복구 중..."
            sudo mv "$config_file.bak" "$config_file"
            sudo service nginx restart
            exit 1
        fi
    else
        echo "오류: Nginx 설정 검사 실패, 백업 복구 중..."
        sudo mv "$config_file.bak" "$config_file"
        sudo service nginx restart
        exit 1
    fi
}

current_port=$(get_current_port)
if [ "$current_port" = "none" ]; then
    deploy "$BLUE_PORT" || exit 1
    switch_traffic "$BLUE_PORT"
    echo "블루-그린 배포 완료: $BLUE_PORT 포트에서만 실행 중"
else
    if [ "$current_port" -eq "$BLUE_PORT" ]; then
        deploy "$GREEN_PORT" || exit 1
        switch_traffic "$GREEN_PORT"
        sudo fuser -k -TERM "$BLUE_PORT/tcp" >/dev/null 2>&1
        echo "블루-그린 배포 완료: $GREEN_PORT 포트에서만 실행 중"
    else
        deploy "$BLUE_PORT" || exit 1
        switch_traffic "$BLUE_PORT"
        sudo fuser -k -TERM "$GREEN_PORT/tcp" >/dev/null 2>&1
        echo "블루-그린 배포 완료: $BLUE_PORT 포트에서만 실행 중"
    fi
fi

sudo rm -f /home/ubuntu/ddarahang/build/libs/ddarahang.jar
