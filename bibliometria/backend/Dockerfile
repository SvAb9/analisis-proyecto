# Despliegue (R4). Compila el backend dentro de la imagen; no hace falta tener Maven ni Java instalados.
FROM maven:3.9-eclipse-temurin-21 AS compilacion
WORKDIR /build
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src src
RUN mvn -q -B package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=compilacion /build/target/bibliometria-0.0.1-SNAPSHOT.jar app.jar
# Los 20 PDF y los vectores GloVe están en el repositorio.
COPY data /app/data
# El modelo de oración de R1 (~90 MB) no se versiona: se descarga al construir la imagen.
ADD https://huggingface.co/sentence-transformers/all-MiniLM-L6-v2/resolve/main/onnx/model.onnx /app/data/modelos/all-MiniLM-L6-v2/model.onnx
ADD https://huggingface.co/sentence-transformers/all-MiniLM-L6-v2/resolve/main/tokenizer.json /app/data/modelos/all-MiniLM-L6-v2/tokenizer.json
EXPOSE 8080
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","app.jar"]
