FROM maven:3.8.5-openjdk-17-slim as build
MAINTAINER Roman Kiryukhin

RUN apt update && apt install -y iputils-ping net-tools netcat

WORKDIR /app
COPY ./pom.xml ./

RUN mvn dependency:resolve

COPY . .
RUN mvn clean package -DskipTests


FROM maven:3.8.5-openjdk-17-slim
WORKDIR /app
ARG JAR_FILE=/target/*.jar
COPY --from=build /app/target/mental-health-0.0.1-SNAPSHOT.jar /mental-health.jar
ENTRYPOINT java -jar /mental-health.jar