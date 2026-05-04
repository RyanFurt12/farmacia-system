# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY common-dtos/pom.xml /common-dtos/pom.xml
COPY common-dtos/src /common-dtos/src
RUN mvn install -f /common-dtos/pom.xml -DskipTests
COPY fornecedor-b-service/pom.xml ./pom.xml
COPY fornecedor-b-service/src ./src
RUN mvn package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
