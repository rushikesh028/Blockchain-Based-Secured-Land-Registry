FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml ./
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app

RUN useradd --system --uid 1001 spring

COPY --from=build /app/target/land-registry-blockchain-1.0.0.jar /app/app.jar
RUN chown -R spring:spring /app

USER spring

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
