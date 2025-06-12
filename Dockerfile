# Use Maven image to build the app
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Use lightweight JDK image to run the app
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Set the port environment variable
ENV PORT=8081
EXPOSE ${PORT}

CMD ["java", "-jar", "app.jar"]
