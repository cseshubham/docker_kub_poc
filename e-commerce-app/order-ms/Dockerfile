# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the fat jar into the container at /app
COPY target/order-ms-0.0.1-SNAPSHOT.jar .

# Make port 8081 available to the world outside this container
EXPOSE 8081

# Run the jar file
ENTRYPOINT ["java","-jar","order-ms-0.0.1-SNAPSHOT.jar"]
