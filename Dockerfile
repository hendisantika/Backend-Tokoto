FROM openjdk:21
WORKDIR /app
COPY . /app
ADD target/spring-management-inventaris.jar spring-management-inventaris.jar
ENTRYPOINT ["java", "-jar", "spring-management-inventaris.jar"]