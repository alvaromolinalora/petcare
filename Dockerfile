# 1. Build stage
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copiamos todo el proyecto a la imagen
COPY . .

# Arreglamos saltos de línea de Windows y damos permisos de ejecución a mvnw
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Compilamos el proyecto sin tests
RUN ./mvnw -B clean package -DskipTests

# 2. Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiamos el JAR generado desde la fase de build
COPY --from=build /app/target/petcare-0.0.1-SNAPSHOT.jar app.jar

# Puerto por defecto de Spring Boot
EXPOSE 8080

# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]
