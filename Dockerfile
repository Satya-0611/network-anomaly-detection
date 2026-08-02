# Stage - 1, Build the Application
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# Stage - 2, Run the Application
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=builder /build/target/network-anomaly-detection-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]