## build stage
FROM maven:3.8.8-eclipse-temurin-21-alpine as build
WORKDIR /app

COPY pom.xml .
COPY src ./src
RUN mvn install -DskipTests=true

## run stage
FROM amazoncorretto:21
WORKDIR /run
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar /run/backend-0.0.1-SNAPSHOT.jar

EXPOSE 8080
ENTRYPOINT java -jar /run/backend-0.0.1-SNAPSHOT.jar