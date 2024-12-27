plugins {
    application
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.kotlin.plugin.spring") version "1.9.25"
    // ↓ JPA/Hibernate 用プラグインなら削除してOK
    // id("org.jetbrains.kotlin.plugin.jpa") version "1.9.25"

    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
    id("com.diffplug.spotless") version "6.22.0"
    id("org.flywaydb.flyway") version "10.10.0"
    // ↓ JPA/Hibernate 用プラグインなら削除してOK
    // kotlin("plugin.allopen") version "2.1.0"
}

dependencies {
    // Spring Boot関連
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

    // Database
    implementation("org.postgresql:postgresql:42.7.4")
    runtimeOnly("com.h2database:h2:2.3.232")

    // jOOQ
    implementation("org.springframework.boot:spring-boot-starter-jooq")
     implementation("org.jooq:jooq:3.18.6")
     implementation("org.jooq:jooq-meta:3.18.6")
     implementation("org.jooq:jooq-meta-extensions:3.18.6")

    // Flyway
    implementation("org.flywaydb:flyway-core:11.1.0")
    implementation("org.flywaydb:flyway-database-postgresql")

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

// ★ Hibernate (noarg/allopen) 用なら削除してOK
// java {
//     toolchain {
//         languageVersion = JavaLanguageVersion.of(21)
//     }
// }

// ★ Kotlin の JVM Toolchain 設定
kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
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
