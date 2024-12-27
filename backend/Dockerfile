FROM openjdk:21-jdk-slim

# 必要なツールをインストール
RUN apt-get update && apt-get install -y \
    postgresql-client \
    inotify-tools \
    ca-certificates \
    curl \
    unzip \
    && rm -rf /var/lib/apt/lists/*

# Gradleキャッシュの設定
ENV GRADLE_USER_HOME=/root/.gradle

# 作業ディレクトリを設定
WORKDIR /app

# Gradle関連ファイルを先にコピーして依存関係をキャッシュ
COPY ./gradlew ./gradlew
COPY ./gradle ./gradle
COPY ./build.gradle.kts ./build.gradle.kts
COPY ./settings.gradle.kts ./settings.gradle.kts

RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon --parallel

# アプリケーションコードを最後にコピー
COPY . .

# 必要に応じてエントリーポイントを設定
CMD ["./gradlew", "bootRun"]
