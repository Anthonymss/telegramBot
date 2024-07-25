# Etapa 1: Construcción
FROM maven:3.9.8-eclipse-temurin-21-alpine AS build

# Crear y establecer el directorio de trabajo
WORKDIR /app

# Copiar los archivos de configuración de Maven, el código fuente y descargar las dependencias
COPY pom.xml ./
RUN mvn dependency:go-offline -B
COPY src ./src

# Construir la aplicación
RUN mvn clean package -DskipTests

# Etapa 2: Ejecutar
FROM eclipse-temurin:21-alpine

# Crear y establecer el directorio de trabajo
WORKDIR /app

# Copiar el archivo JAR construido desde la etapa de construcción
COPY --from=build /app/target/*.jar /app/app.jar

# Exponer el puerto de la aplicación
EXPOSE 8082

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "/app/app.jar"]