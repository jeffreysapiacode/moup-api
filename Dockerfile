FROM eclipse-temurin:17
COPY target/moup-api.jar moup-api.jar
ENTRYPOINT ["java","-Dspring.profiles.active=production","-jar","/moup-api.jar"]