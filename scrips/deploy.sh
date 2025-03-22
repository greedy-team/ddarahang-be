#!/bin/bash

# 포트 설정
BLUE_PORT=8080
GREEN_PORT=8081

# 환경 변수 체크
: "${RDS_HOST?RDS_HOST is required}"
: "${RDS_USERNAME?RDS_USERNAME is required}"
: "${RDS_PASSWORD?RDS_PASSWORD is required}"
: "${GOOGLE_SPREADSHEET_ID?GOOGLE_SPREADSHEET_ID is required}"
: "${GOOGLE_SERVICE_ACCOUNT_PATH?GOOGLE_SERVICE_ACCOUNT_PATH is required}"

timestamp=$(date +"%Y%m%d%H%M%S")

health_check() {
    local port=$1
    local CHECK_URL="http://localhost:$port/actuator/health"
    local RETRY_COUNT=0
    local MAX_RETRY=10

    echo "Checking health on port $port..."

    while [ $RETRY_COUNT -lt $MAX_RETRY ]; do
        if curl_output=$(curl --silent --head "$CHECK_URL" 2>&1); then
            echo "Health check passed on port $port"
            return 0
        fi
        echo "Retry $((RETRY_COUNT+1))/$MAX_RETRY: $curl_output"
        sleep 5
        RETRY_COUNT=$((RETRY_COUNT+1))
    done

    echo "Health check failed on port $port after $MAX_RETRY retries"
    return 1
}

rollback() {
    local port=$1
    local backup_jar="/home/ubuntu/ddarahang/old_build/app-${port}-${timestamp}.jar"
    local current_version_jar="/home/ubuntu/ddarahang/app-${port}.jar"

    echo "Rolling back on port $port..."

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
        echo "No backup JAR found for rollback on port $port"
        return 1
    fi
}

deploy() {
    local port=$1
    local app_dir="/home/ubuntu/ddarahang"
    local current_version_jar="$app_dir/app-${port}.jar"
    local new_version_jar="$app_dir/build/libs/ddarahang.jar"
    local backup_dir="$app_dir/old_build"

    echo "Deploying new version to port $port..."

    if [ ! -f "$new_version_jar" ]; then
        echo "Error: $new_version_jar not found. Aborting..."
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
    health_check "$port" || { rollback "$port"; exit 1; }

    echo "Deployment successful on port $port"
}

get_current_port() {
    if sudo fuser $BLUE_PORT/tcp >/dev/null 2>&1 && ! sudo fuser $GREEN_PORT/tcp >/dev/null 2>&1; then
        echo "$BLUE_PORT"
    elif sudo fuser $GREEN_PORT/tcp >/dev/null 2>&1 && ! sudo fuser $BLUE_PORT/tcp >/dev/null 2>&1; then
        echo "$GREEN_PORT"
    else
        echo "none"
    fi
}

switch_traffic() {
    local old_port=$1
    local new_port=$2
    sudo sed -i '/upstream backend {/,/}/ s/server 127.0.0.1:'"$old_port"';//g' /etc/nginx/sites-available/default
    sudo sed -i '/upstream backend {/,/}/ s/server 127.0.0.1:'"$new_port"' down;/server 127.0.0.1:'"$new_port"';/' /etc/nginx/sites-available/default
    sudo service nginx reload
}

current_port=$(get_current_port)
case "$current_port" in
    "$BLUE_PORT")
        echo "$BLUE_PORT is running, deploying to $GREEN_PORT..."
        deploy "$GREEN_PORT" || exit 1
        switch_traffic "$BLUE_PORT" "$GREEN_PORT"
        sudo fuser -k -TERM "$BLUE_PORT"/tcp
        echo "Blue-Green deployment complete. Only port $GREEN_PORT is running."
        ;;
    "$GREEN_PORT")
        echo "$GREEN_PORT is running, deploying to $BLUE_PORT..."
        deploy "$BLUE_PORT" || exit 1
        switch_traffic "$GREEN_PORT" "$BLUE_PORT"
        sudo fuser -k -TERM "$GREEN_PORT"/tcp
        echo "Blue-Green deployment complete. Only port $BLUE_PORT is running."
        ;;
    *)
        echo "Error: Both ports are either running or stopped. Manual intervention required."
        exit 1
        ;;
esac

sudo rm /home/ubuntu/ddarahang/build/libs/ddarahang.jar
