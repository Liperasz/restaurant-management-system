FROM eclipse-temurin:26-jdk-alpine AS build
WORKDIR /app
COPY mvnw pom.xml ./
COPY src ./src
RUN chmod +x mvnw && ./mvnw package -DskipTests

FROM eclipse-temurin:26-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
