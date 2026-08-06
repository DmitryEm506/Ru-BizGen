# syntax=docker/dockerfile:1.7

# ===== builder =====
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /work

# Сначала копируем обёртку Gradle и метаданные сборки (для кэша слоёв)
COPY settings.gradle.kts build.gradle.kts gradle.properties ./
COPY gradle/ gradle/
COPY gradlew gradlew.bat ./
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# Исходники модулей, нужные для конфигурации и сборки :ru-bizgen-mcp.
# ru-bizgen-plugin включён, т.к. его build.gradle.kts читает
# src/main/resources/META-INF/description.html на этапе конфигурации.
COPY ru-bizgen-core/ ru-bizgen-core/
COPY ru-bizgen-mcp/ ru-bizgen-mcp/
COPY ru-bizgen-plugin/ ru-bizgen-plugin/
COPY build-logic/ build-logic/

# ru-bizgen-perf и ru-bizgen-archunit не нужны для сборки MCP,
# но Gradle требует существования директорий всех включённых в settings.gradle.kts модулей.
RUN mkdir -p ru-bizgen-perf ru-bizgen-archunit

RUN --mount=type=cache,target=/root/.gradle \
    --mount=type=cache,target=/root/.konan \
    ./gradlew :ru-bizgen-mcp:installDist --no-daemon -x test

# ===== runtime =====
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*
COPY --from=builder /work/ru-bizgen-mcp/build/install/ru-bizgen-mcp /app

EXPOSE 8081
ENTRYPOINT ["/app/bin/ru-bizgen-mcp"]
CMD ["--host=0.0.0.0", "--port=8081"]
