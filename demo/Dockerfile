FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
# Copiar pom.xml primero para cachear dependencias (si el pom no cambia, esta capa se reutiliza)
COPY ./servidorSQL/demo/pom.xml .
COPY ./servidorSQL/demo/src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/demo-*.jar app.jar
COPY ./scrapeo/*.csv ./scrapeo/
ENTRYPOINT ["java", "-jar", "app.jar"]