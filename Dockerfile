# Etapa 1: Build (Compilação)
# Usamos o Maven com JDK 21 para gerar o .jar
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Runtime (Execução)
# Usamos uma imagem leve apenas com o JRE 21 para rodar a aplicação
FROM eclipse-temurin:21-jre-jammy
COPY --from=build /target/demo-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]