# Etapa 1: compila e gera o .jar com Maven (os testes rodam fora do Docker, com ./mvnw test)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# baixa as dependências numa camada separada; só refaz quando o pom.xml muda
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q package -DskipTests

# Etapa 2: imagem final só com o JRE e o .jar
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# roda sem root; a pasta de logs precisa ser do usuário da aplicação
RUN addgroup -S musica && adduser -S musica -G musica && mkdir logs && chown musica:musica logs
USER musica

COPY --from=build /build/target/musica-*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=10s --timeout=3s --start-period=40s --retries=5 \
    CMD wget -qO- http://localhost:8080/actuator/health | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
