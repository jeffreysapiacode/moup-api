FROM eclipse-temurin:17
COPY target/moup-api.jar moup-api.jar
ENTRYPOINT ["java","-Dspring.profiles.active=production","-jar","/moup-api.jar"]


#mvn clean install
#docker build -t moup-api .
#docker kill moup-api
#docker rm moup-api
#docker run -d -v moup_api_volume:/content --name moup-api -p 8081:8081 --restart always moup-api

