FROM eclipse-temurin:17
COPY target/moup-api.jar moup-api.jar
ENTRYPOINT ["java","-Dspring.profiles.active=production","-Xms2G","-Xmx4G","-jar","/moup-api.jar"]

# 5/14/2026 12:57AM
# Build and Deploy to AWS EC2 Instance
# mvn clean install
# docker build --platform linux/amd64,linux/arm64 -t moup-api .
# docker tag moup-api:latest registry.moup.io/moup-api:latest
# docker push registry.moup.io/moup-api:latest

# ON REMOTE INSTANCE
# ssh -i "~/Downloads/moup-macbook-air.pem" ec2-user@3.147.184.206 sudo docker pull registry.moup.io/moup-api:latest
# ssh -i "~/Downloads/moup-macbook-air.pem" ec2-user@3.147.184.206 sudo docker kill moup-api
# ssh -i "~/Downloads/moup-macbook-air.pem" ec2-user@3.147.184.206 sudo docker rm moup-api
# ssh -i "~/Downloads/moup-macbook-air.pem" ec2-user@3.147.184.206 sudo docker run -d -v moup_api_volume:/content --name moup-api -p 8081:8081 --restart always registry.moup.io/moup-api:latest

# mvn clean install && docker build --platform linux/amd64,linux/arm64 -t moup-api . && docker tag moup-api:latest registry.moup.io/moup-api:latest && docker push registry.moup.io/moup-api:latest && ssh -i "~/Downloads/moup-macbook-air.pem" ec2-user@3.147.184.206 sudo docker pull registry.moup.io/moup-api:latest && ssh -i "~/Downloads/moup-macbook-air.pem" ec2-user@3.147.184.206 sudo docker kill moup-api && ssh -i "~/Downloads/moup-macbook-air.pem" ec2-user@3.147.184.206 sudo docker rm moup-api && ssh -i "~/Downloads/moup-macbook-air.pem" ec2-user@3.147.184.206 sudo docker run -d -v moup_api_volume:/content --name moup-api -p 8081:8081 --restart always registry.moup.io/moup-api:latest