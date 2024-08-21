# Start from a base image
FROM mcr.microsoft.com/playwright/java:v1.30.0-focal as build

RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y maven

# Set the working directory
WORKDIR /app

# Copy pom.xml and get all dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the application code
COPY src ./src

# Build the application
RUN mvn package

# Start a new stage to minimize the final image size
FROM mcr.microsoft.com/playwright/java:v1.30.0-focal as package

# Set the working directory
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/target/*-jar-with-dependencies.jar ./app.jar

# Run the application
CMD ["java", "-jar", "/app/app.jar"]