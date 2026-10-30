# Despliegue (R4): construir con 'mvn package' y luego la imagen
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/bibliometria-0.0.1-SNAPSHOT.jar app.jar
COPY data /app/data
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
