# Use uma imagem base do OpenJDK 17
FROM openjdk:17-alpine

# Defina o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copie o arquivo pom.xml e os arquivos de código-fonte para o diretório de trabalho
COPY pom.xml .
COPY src ./src

# Instale o Maven (caso não esteja incluído na imagem base)
RUN apk add --no-cache maven

# Execute o comando Maven para compilar o projeto
RUN mvn clean package -DskipTests

# Exponha a porta onde a aplicação será executada (ajuste conforme necessário)
EXPOSE 8080

# Comando padrão para executar a aplicação quando o contêiner for iniciado
CMD ["java", "-jar", "target/app-0.0.1-SNAPSHOT.jar"]
