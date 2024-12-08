# Etapa 1: Construção da aplicação (imagem Maven)
FROM maven:3.8.4-openjdk-17-slim AS builder

WORKDIR /app

# Copiar arquivos do projeto para o contêiner
COPY pom.xml .
COPY src ./src
#COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Imagem final para rodar a aplicação (imagem JDK leve)
FROM azul/zulu-openjdk-alpine:17

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
