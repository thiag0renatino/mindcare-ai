FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /workspace

COPY pom.xml .
RUN mvn -B -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

# Copia o jar
COPY --from=builder /workspace/target/*SNAPSHOT.jar app.jar

# Porta padrão do Spring Boot
EXPOSE 8080

USER spring
ENTRYPOINT ["java", "-jar", "/app/app.jar"]