FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml /app/pom.xml
COPY src /app/src
RUN mvn clean package -DskipTests

FROM openjdk:21-slim-bullseye

RUN apt-get update && apt-get install -y postgresql-client
WORKDIR /app

COPY --from=build /app/target/user-service-*.jar /app/app.jar
COPY wait-for-postgres.sh /app/wait-for-postgres.sh
RUN chmod +x /app/wait-for-postgres.sh

ENTRYPOINT ["/app/wait-for-postgres.sh", "java", "-jar", "/app/app.jar"]
