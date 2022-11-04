plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.21"
    id("org.jetbrains.kotlin.kapt") version "1.6.21"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.6.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.micronaut.application") version "3.6.3"
    id("io.micronaut.docker") version "3.6.3"
}

version = "0.1"
group = "PrismIntegration"

val kotlinVersion=project.properties.get("kotlinVersion")

repositories {
    mavenCentral()
    mavenLocal()
    google()
    maven("https://plugins.gradle.org/m2/")
    // Required for Kotlin coroutines that support new memory management mode
    maven {
        url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-coroutines/maven")
    }
    maven {
        url = uri("https://maven.pkg.github.com/input-output-hk/atala-prism-sdk")
        credentials {
            username = "atala-dev"
            password = System.getenv("PRISM_SDK_PASSWORD")
        }
    }
}

dependencies {
    kapt("io.micronaut:micronaut-http-validation")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-jackson-databind")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("jakarta.annotation:jakarta.annotation-api")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    runtimeOnly("ch.qos.logback:logback-classic")
    implementation("io.micronaut:micronaut-validation")

    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Cryptography primitives support
    implementation("io.iohk.atala:prism-crypto:v1.4.1")
    // Decentralized Identifiers (DIDs) operations support
    implementation("io.iohk.atala:prism-identity:v1.4.1")
    // Verifiable Credentials (VCs) support
    implementation("io.iohk.atala:prism-credentials:v1.4.1")
    // Atala PRISM Node API service support
    implementation("io.iohk.atala:prism-api:v1.4.1")
    // JSON library to work with credentials content (update to the latest version)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
    // Datetime support (update to the latest version)
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.2.1")
    // Fixes a bug from SLF4J for bitcoinj library (update to the latest version)
    implementation("org.slf4j:slf4j-simple:1.7.32")

    // json serialiser
    implementation ("com.google.code.gson:gson:2.10")

}

application {
    mainClass.set("PrismIntegration.ApplicationKt")
}
java {
    sourceCompatibility = JavaVersion.toVersion("17")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}
graalvmNative.toolchainDetection.set(false)
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("PrismIntegration.*")
    }
}



