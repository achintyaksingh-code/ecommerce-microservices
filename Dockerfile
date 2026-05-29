FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY shared shared
COPY platform platform
COPY services services
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre
ARG SERVICE_PATH
WORKDIR /app
COPY --from=build /app/${SERVICE_PATH}/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
