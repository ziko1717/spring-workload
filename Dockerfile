# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the executable JAR file from the host machine to the container
COPY out/artifacts/spring_workload_jar/spring-workload.jar /app/your-application.jar

# Expose the port your application runs on
EXPOSE 9120

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "/app/your-application.jar"]