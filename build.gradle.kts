import com.adarshr.gradle.testlogger.theme.ThemeType.MOCHA_PARALLEL
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN

plugins {
    val kotlinVersion = "1.3.71"
    kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("com.adarshr.test-logger") version "2.0.0"
    id("com.dorongold.task-tree") version "1.5"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
    application
//    idea
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")

    // Cron Dependencies
    implementation("org.quartz-scheduler:quartz:2.3.2")
    implementation("com.cronutils:cron-utils:9.0.1")

    // JSON Dependencies
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")
    implementation("com.google.code.gson:gson:2.8.6")
    val jacksonVersion = "2.10.3"
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")

    // Logging Dependencies
    implementation("io.github.microutils:kotlin-logging:1.7.9")
    runtimeOnly("org.slf4j:slf4j-api:1.7.30")
    val log4jVersion = "2.13.1"
    runtimeOnly("org.apache.logging.log4j:log4j-api:$log4jVersion")
    runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
    runtimeOnly("com.amazonaws:aws-lambda-java-log4j2:1.0.0")

    // Database Dependencies
    val exposedVersion = "0.23.1"
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    runtimeOnly("org.postgresql:postgresql:42.2.2")
    implementation("org.postgresql:postgresql:42.2.2")

    // Test Dependencies
    val junitVersion = "5.6.1"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")

    val kotestVersion = "4.0.3"
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion") // for kotest framework
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion") // for kotest core jvm assertions
    testImplementation("io.kotest:kotest-property-jvm:$kotestVersion") // for kotest property test

    testImplementation("io.strikt:strikt-core:0.24.0")
    testImplementation("io.mockk:mockk:1.10.0")

    val testContainersVersion = "1.14.0"
    testImplementation("org.testcontainers:testcontainers:$testContainersVersion")
    testImplementation("org.testcontainers:postgresql:$testContainersVersion")
}

tasks {

    build {
        dependsOn("shadowJar")
    }

    test {
        useJUnitPlatform()
        maxParallelForks = Runtime.getRuntime().availableProcessors().minus(1).coerceAtLeast(1)
        testlogger {
            theme = MOCHA_PARALLEL
        }
    }

    jar {
        archiveBaseName.set("lambda")
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_1_8.toString()
        }
    }

    withType<ShadowJar> {
        archiveBaseName.set(project.tasks.jar.get().archiveBaseName)
        mergeServiceFiles()
        minimize()
    }

    application {
        mainClassName = "me.qoomon.examples.MainKt"
    }

    withType<JavaExec> {
        standardInput = System.`in`
    }
}


ktlint {
    ignoreFailures.set(true)
    reporters {
        reporter(PLAIN)
        reporter(CHECKSTYLE)
    }
}
