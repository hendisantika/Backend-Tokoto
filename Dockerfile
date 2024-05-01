FROM openjdk:21-jdk
ARG JAR_FILE=target/*.jar
COPY ./target/spring-management-inventaris.jar app.jar
ADD target/spring-management-inventaris.jar spring-management-inventaris.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]