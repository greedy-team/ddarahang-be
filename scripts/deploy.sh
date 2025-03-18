#!/bin/bash

# 애플리케이션 포트 설정
PORT1=8081
PORT2=8082
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
    local backup_jar="/home/ubuntu/cicd/old_build/app-${port}-${timestamp}.jar"
    local current_version_jar="/home/ubuntu/cicd/app-${port}.jar"

    echo "Rolling back on port $port..."

    # 새 버전 프로세스 종료
    sudo fuser -k -TERM $port/tcp

    # 백업된 기존 버전 복원
    mv $backup_jar $current_version_jar

    # 이전 버전 실행
    sudo nohup java -jar -Dserver.port=$port $current_version_jar > /home/ubuntu/console-$port.log 2>&1 &

    # Nginx 설정 파일 원래 상태로 복구
    sudo sed -i '/upstream backend {/,/}/ s/server 127.0.0.1:'"$port"' down;/server 127.0.0.1:'"$port"';/' /etc/nginx/sites-available/default
    sudo service nginx reload
}

# 배포 함수
deploy() {
    local port=$1
    local current_version_jar="/home/ubuntu/cicd/app-${port}.jar"
    local new_version_jar=$(ls /home/ubuntu/cicd/ZeroDownTimeDeployment-*.jar | head -n 1)

    echo "Deploying new version to port $port..."

    # 기존 버전 백업
    mv $current_version_jar /home/ubuntu/cicd/old_build/app-${port}-${timestamp}.jar

    # 새 버전 배포
    cp $new_version_jar $current_version_jar
    sudo chmod +x $current_version_jar
    sudo nohup java -jar -Dserver.port=$port $current_version_jar > /home/ubuntu/console-$port.log 2>&1 &

    sleep 20

    # 헬스 체크
    health_check $port
    if [ $? -ne 0 ]; then
        rollback $port
        return 1
    fi

    echo "Deployment successful on port $port"
    return 0
}

# 8081에 대한 배포
echo "Routing traffic away from port $PORT1..."
sudo sed -i '/upstream backend {/,/}/ s/server 127.0.0.1:'"$PORT1"';/server 127.0.0.1:'"$PORT1"' down;/' /etc/nginx/sites-available/default
sudo service nginx reload

# 8081 포트에서 실행 중인 프로세스 종료
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

# 8082에 대한 배포
echo "Routing traffic away from port $PORT2..."
sudo sed -i '/upstream backend {/,/}/ s/server 127.0.0.1:'"$PORT2"';/server 127.0.0.1:'"$PORT2"' down;/' /etc/nginx/sites-available/default
sudo service nginx reload

# 8082 포트에서 실행 중인 프로세스 종료
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
sudo rm /home/ubuntu/cicd/no_downtime-*.jar

echo "Rolling deployment complete. Both ports are now running the new version."