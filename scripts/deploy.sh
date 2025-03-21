#!/bin/bash

# 애플리케이션 포트 설정
PORT1=8080
PORT2=8081
DEPLOY_PORT=0

# 헬스 체크 함수
health_check() {
    local port=$1
    local CHECK_URL="http://localhost:$port/actuator/health"
    local RETRY_COUNT=0
    local MAX_RETRY=10

    echo "Checking health on port $port..."

    until $(curl --output /dev/null --silent --head --fail $CHECK_URL); do
        sleep 5
        RETRY_COUNT=$((RETRY_COUNT+1))
        if [ $RETRY_COUNT -eq $MAX_RETRY ]; then
            echo "Health check failed on port $port"
            return 1
        fi
    done

    echo "Health check passed on port $port"
    return 0
}

timestamp=$(date +"%Y%m%d%H%M%S")

# 롤백 함수
rollback() {
    local port=$1
    local backup_jar="/home/ubuntu/ddarahang/old_build/app-${port}-${timestamp}.jar"
    local current_version_jar="/home/ubuntu/ddarahang/app-${port}.jar"

    echo "Rolling back on port $port..."

    sudo fuser -k -TERM $port/tcp
    mv $backup_jar $current_version_jar
    sudo nohup java -jar \
      -DRDS_HOST="${RDS_HOST}" \
      -DRDS_USERNAME="${RDS_USERNAME}" \
      -DRDS_PASSWORD="${RDS_PASSWORD}" \
      -DGOOGLE_SPREADSHEET_ID="${GOOGLE_SPREADSHEET_ID}" \
      -DGOOGLE_SERVICE_ACCOUNT_PATH="${GOOGLE_SERVICE_ACCOUNT_PATH}" \
      -Dserver.port=$port $current_version_jar > /home/ubuntu/console-$port.log 2>&1 &
    sudo sed -i '/upstream backend {/,/}/ s/server 127.0.0.1:'"$port"' down;/server 127.0.0.1:'"$port"';/' /etc/nginx/sites-available/default
    sudo service nginx reload
}

# 배포 함수
deploy() {
    local port=$1
    local current_version_jar="/home/ubuntu/ddarahang/app-${port}.jar"
    local new_version_jar="/home/ubuntu/ddarahang/ddarahang.jar"

    echo "Deploying new version to port $port..."

    if [ ! -f "$new_version_jar" ]; then
        echo "Error: $new_version_jar not found. Aborting..."
        exit 1
    fi

    mkdir -p /home/ubuntu/ddarahang/old_build
    [ -f "$current_version_jar" ] && mv $current_version_jar /home/ubuntu/ddarahang/old_build/app-${port}-${timestamp}.jar
    cp $new_version_jar $current_version_jar
    sudo chmod +x $current_version_jar
    sudo nohup java -jar \
      -DRDS_HOST="${RDS_HOST}" \
      -DRDS_USERNAME="${RDS_USERNAME}" \
      -DRDS_PASSWORD="${RDS_PASSWORD}" \
      -DGOOGLE_SPREADSHEET_ID="${GOOGLE_SPREADSHEET_ID}" \
      -DGOOGLE_SERVICE_ACCOUNT_PATH="${GOOGLE_SERVICE_ACCOUNT_PATH}" \
      -Dserver.port=$port $current_version_jar > /home/ubuntu/console-$port.log 2>&1 &

    sleep 20

    health_check $port
    if [ $? -ne 0 ]; then
        rollback $port
        return 1
    fi

    echo "Deployment successful on port $port"
    return 0
}

if [ -n "$(sudo fuser 8080/tcp)" ] && [ -z "$(sudo fuser 8081/tcp)" ]; then
    echo "8080 is running, 8081 is not running. Deploying new version to 8081 first..."
    deploy $PORT2
    if [ $? -ne 0 ]; then
        echo "Deployment failed on port $PORT2. Aborting..."
        exit 1
    fi
    echo "Re-routing traffic to port $PORT2..."
    sudo sed -i '/upstream backend {/,/}/ s/server 127.0.0.1:'"$PORT2"' down;/server 127.0.0.1:'"$PORT2"';/' /etc/nginx/sites-available/default
    sudo service nginx reload
fi

echo "Routing traffic away from port $PORT1..."
sudo sed -i '/upstream backend {/,/}/ s/server 127.0.0.1:'"$PORT1"';/server 127.0.0.1:'"$PORT1"' down;/' /etc/nginx/sites-available/default
sudo service nginx reload

echo "Stopping process on port $PORT1..."
sudo fuser -k -TERM $PORT1/tcp
if [ $? -ne 0 ]; then
    echo "Failed to stop the process on port $PORT1. Aborting..."
    exit 1
fi
echo "Process on port $PORT1 stopped."

deploy $PORT1
if [ $? -ne 0 ]; then
    echo "Deployment failed on port $PORT1. Aborting..."
    exit 1
fi

echo "Re-routing traffic to port $PORT1..."
sudo sed -i '/upstream backend {/,/}/ s/server 127.0.0.1:'"$PORT1"' down;/server 127.0.0.1:'"$PORT1"';/' /etc/nginx/sites-available/default
sudo service nginx reload

echo "Routing traffic away from port $PORT2..."
sudo sed -i '/upstream backend {/,/}/ s/server 127.0.0.1:'"$PORT2"';/server 127.0.0.1:'"$PORT2"' down;/' /etc/nginx/sites-available/default
sudo service nginx reload

echo "Stopping process on port $PORT2..."
sudo fuser -k -TERM $PORT2/tcp
if [ $? -ne 0 ]; then
    echo "Failed to stop the process on port $PORT2. Aborting..."
    exit 1
fi
echo "Process on port $PORT2 stopped."

deploy $PORT2
if [ $? -ne 0 ]; then
    echo "Deployment failed on port $PORT2. Aborting..."
    exit 1
fi

echo "Re-routing traffic to port $PORT2..."
sudo sed -i '/upstream backend {/,/}/ s/server 127.0.0.1:'"$PORT2"' down;/server 127.0.0.1:'"$PORT2"';/' /etc/nginx/sites-available/default
sudo service nginx reload
sudo rm /home/ubuntu/ddarahang/ddarahang.jar

echo "Rolling deployment complete. Both ports are now running the new version."
