# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY common-dtos/pom.xml /common-dtos/pom.xml
COPY common-dtos/src /common-dtos/src
RUN mvn install -f /common-dtos/pom.xml -DskipTests

COPY cpf-validator/pom.xml /cpf-validator/pom.xml
COPY cpf-validator/src /cpf-validator/src
RUN mvn install -f /cpf-validator/pom.xml -DskipTests

COPY farmacia-app/pom.xml ./pom.xml
RUN mvn dependency:go-offline -B
COPY farmacia-app/src ./src
RUN mvn package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
