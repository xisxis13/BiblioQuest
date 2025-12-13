# Image de base Ubuntu 24.04
FROM maven:3.9.9-eclipse-temurin-23-alpine AS build
# Nom de l'auteur
LABEL author="62491"

# Création d'un répertoire de travail pour l'application
WORKDIR /app

# Copie du fichier JAR de la machine locale vers le conteneur
COPY pom.xml .
COPY src ./src

# Compiler le projet
RUN mvn package -Dmaven.test.skip=true

FROM eclipse-temurin:23-jre-alpine

WORKDIR /app

# Copie le jar depuis l'étape de build
COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]