### Instructions for Building Docker Image ###

# Stage 1: Build the Spring Boot application
#1 JDK and Assign this Stage Name to be 'build'
FROM openjdk:21-jdk-slim AS build

#2 Set Working Directory
WORKDIR /app

#3 Docker Layer Caching Optimization: Reused if not change
#3.1 Copy Build and Setting Layer
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

#3.2 Install findutils (contains xargs)
RUN apt-get update && apt-get install -y findutils && rm -rf /var/lib/apt/lists/*

#3.3 Create Dependencies Layer and Prevents Gradle from running as a background daemon
RUN ./gradlew dependencies --no-daemon

#4 Copy Entire Src
COPY src src

#5 Execution for Packaging to JAR file
RUN ./gradlew bootJar --no-daemon

# Stage 2: Create the final production image
#1 JDK
FROM openjdk:21-jdk-slim

#2 Set Working Directory
WORKDIR /app

#3 Copy Compiled Java App (JAR) from 'build' Stage to WORKDIR and Renamed to app.jar
COPY --from=build /app/build/libs/*.jar app.jar

#4 Listen Port
EXPOSE 8080

#5 Excute JVM and program packaged in Jar file
ENTRYPOINT ["java", "-jar", "app.jar"]