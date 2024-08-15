FROM maven:3.8.5-openjdk-17 as build
MAINTAINER Roman Kiryukhin
ADD . /app
WORKDIR /app
CMD apt update
CMD apt install net-tools
CMD apt install iputils-ping
RUN mvn clean package

FROM maven:3.8.5-openjdk-17
ARG JAR_FILE=/target/*.jar
COPY --from=build /app/target/mental-health-0.0.1-SNAPSHOT.jar /mental-health.jar
EXPOSE 8888
ENTRYPOINT java -jar /mental-health.jar