FROM openjdk:11-jdk-slim
EXPOSE 8888
ADD target/CloudStorage-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]