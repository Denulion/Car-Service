FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests
RUN apt-get update && apt-get install -y openssl && \
    openssl genpkey -algorithm RSA -out src/main/resources/app.key -pkeyopt rsa_keygen_bits:2048 && \
    openssl rsa -pubout -in src/main/resources/app.key -out src/main/resources/app.pub

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=builder /build/target/techin-0.0.1-SNAPSHOT.jar app.jar
COPY wait-for-it.sh wait-for-it.sh

RUN chmod +x wait-for-it.sh

EXPOSE 8080

ENTRYPOINT ["./wait-for-it.sh", "mysql:3306", "--timeout=60", "--", "java", "-jar", "app.jar"]