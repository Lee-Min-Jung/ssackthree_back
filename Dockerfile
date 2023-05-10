FROM adoptopenjdk/openjdk11:jdk-11.0.11_9-alpine-slim
ARG JAR_FILE_PATH=build/libs/*.jar
COPY ${JAR_FILE_PATH} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
