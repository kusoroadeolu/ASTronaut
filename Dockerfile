FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:resolve

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
LABEL authors="eastw"

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 9093
ENTRYPOINT ["java", "-jar", "app.jar"]