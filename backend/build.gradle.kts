import nu.studer.gradle.jooq.JooqEdition
import org.jooq.meta.jaxb.Logging
import org.jooq.meta.jaxb.Property

buildscript {
    dependencies {
        // Flyway for PostgreSQL (バージョンは必要に応じて調整)
//        classpath("org.flywaydb:flyway-database-postgresql:10.22.0")
    }
}

plugins {
    application
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
    id("com.diffplug.spotless") version "6.22.0"
    id("org.flywaydb.flyway") version "11.1.0"
    id("nu.studer.jooq") version "9.0"
}

repositories {
    mavenCentral()
}

configurations {
    create("flywayMigration")
}

dependencies {
    // Spring Boot 関連
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-devtools")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.25")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.25")

    // JSON/JWT
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // PostgreSQL (アプリ起動時用)
    implementation("org.postgresql:postgresql:42.7.4")

    // jOOQ 関連
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.jooq:jooq:3.19.16")
    implementation("org.jooq:jooq-meta:3.19.16")
    implementation("org.jooq:jooq-codegen:3.19.16")
    implementation("org.jooq:jooq-postgres-extensions:3.19.16")

    // ★ jOOQのコード生成時に使うPostgreSQLドライバ
//    jooqGenerator("org.postgresql:postgresql:42.7.4")
    jooqGenerator("com.h2database:h2:2.3.232")


    // Flyway
    implementation("org.flywaydb:flyway-core:11.1.0")

    // H2
    add("flywayMigration", "com.h2database:h2:2.3.232")

    // テスト関連
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("io.kotest:kotest-property:5.9.0")
    testImplementation("io.kotest:kotest-runner-junit5:5.9.0")
    testImplementation("io.kotest:kotest-assertions-core:5.9.0")
    testImplementation("io.mockk:mockk:1.13.5")

    // AWS
    implementation(platform("io.awspring.cloud:spring-cloud-aws-dependencies:3.1.0"))
    implementation(platform("software.amazon.awssdk:bom:2.26.7"))
    implementation("io.awspring.cloud:spring-cloud-aws-starter-parameter-store")
    implementation("software.amazon.awssdk:s3")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

spotless {
    kotlin {
        ktfmt().googleStyle()
        target("src/**/*.kt")
    }
}

application {
    mainClass.set("rankifyHub.RankifyHubApplicationKt")
}

val dbUrl = System.getenv("JOOQ_DB_URL") ?: "jdbc:postgresql://localhost:5432/my_database"
// Flyway設定
flyway {
    configurations = arrayOf("flywayMigration")
    url = "jdbc:h2:/tmp/my_database;AUTO_SERVER=TRUE"
    user = "sa"
    password = ""
}

jooq {
    version.set("3.19.16")
    edition.set(JooqEdition.OSS)

    configurations {
        create("main") {
            jooqConfiguration.apply {
                logging = Logging.WARN
                jdbc.apply {
//                    driver = "org.postgresql.Driver"
//                    url = dbUrl
//                    user = "user"
//                    password = "password"
                    driver = "org.h2.Driver"
                    url = "jdbc:h2:/tmp/my_database;AUTO_SERVER=TRUE"
                    user = "sa"
                    password = ""
                    properties = listOf(
                        Property().apply {
                            key = "PAGE_SIZE"
                            value = "2048"
                        }
                    )
                }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
//                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        name = "org.jooq.meta.h2.H2Database"
                        inputSchema = "PUBLIC"
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = false
                        isImmutablePojos = false
                        isFluentSetters = false
                    }
                    target.apply {
                        packageName = "rankifyHub"
                        directory = "build/generated-src/jooq/main"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}

// jOOQコード生成タスク
tasks.named("generateJooq") {
    dependsOn(tasks.named("flywayMigrate"))

    inputs.files(fileTree("src/main/resources/db/migration"))
        .withPropertyName("migrations")
        .withPathSensitivity(PathSensitivity.RELATIVE)

    // 常に実行したい場合は false
    outputs.upToDateWhen { false }
}

// jOOQ バージョンを強制統一
configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jooq") {
            useVersion("3.19.16")
        }
        if (requested.group == "org.flywaydb" && requested.name == "flyway-core") {
            useVersion("11.1.0")
            because("We want to override the Flyway version to 11.1.0")
        }
    }
}
