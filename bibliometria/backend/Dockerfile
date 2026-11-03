# Imagen del backend: compila el proyecto e incluye los PDF y los modelos de R1.
FROM maven:3.9-eclipse-temurin-21 AS compilacion
WORKDIR /build
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src src
RUN mvn -q -B package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=compilacion /build/target/bibliometria-0.0.1-SNAPSHOT.jar app.jar
COPY data /app/data
ADD https://huggingface.co/sentence-transformers/all-MiniLM-L6-v2/resolve/main/onnx/model.onnx /app/data/modelos/all-MiniLM-L6-v2/model.onnx
ADD https://huggingface.co/sentence-transformers/all-MiniLM-L6-v2/resolve/main/tokenizer.json /app/data/modelos/all-MiniLM-L6-v2/tokenizer.json
EXPOSE 8080
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","app.jar"]
