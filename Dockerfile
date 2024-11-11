FROM openjdk:17-jdk-slim AS build

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline

COPY src ./src


RUN ./mvnw clean test package  # This will run tests and then create the JAR file

FROM openjdk:17-jdk-slim


WORKDIR /app

COPY --from=build /app/target/*.jar points-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "points-0.0.1-SNAPSHOT.jar"]
