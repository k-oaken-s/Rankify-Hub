FROM openjdk:21-jdk-slim AS build
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
# ビルド時はデバッグオプションを使用しない
RUN ./gradlew bootJar --no-daemon

FROM openjdk:21-jdk-slim
WORKDIR /app

# 開発環境でのみ必要なツールをARGで制御
ARG ENVIRONMENT=prod
RUN if [ "$ENVIRONMENT" = "dev" ]; then \
    apt-get update && apt-get install -y \
    postgresql-client \
    inotify-tools \
    curl \
    unzip; \
    fi

# 常に必要な最小限のツール
RUN apt-get update && apt-get install -y \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/build/libs/*.jar app.jar

# アプリケーションポートを公開
EXPOSE 8080
# デバッグポートは常に定義しておく（使用するかはdocker-compose.ymlで制御）
EXPOSE 5005

# 環境に応じて起動コマンドとデバッグオプションを設定
CMD if [ "$ENVIRONMENT" = "dev" ]; then \
    java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar app.jar; \
    else \
    java -jar app.jar; \
    fi