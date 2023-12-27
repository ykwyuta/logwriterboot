FROM amazoncorretto:17-al2023-jdk

COPY ./build/libs/demo-0.0.1-SNAPSHOT.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]