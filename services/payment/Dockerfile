# Etapa de construcción
FROM maven:3.8.8-eclipse-temurin-17-alpine AS build
WORKDIR /home/app
COPY pom.xml ./
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
EXPOSE 8060
COPY --from=build /home/app/target/*.jar /app/app.jar
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]