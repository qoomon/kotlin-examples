import com.adarshr.gradle.testlogger.theme.ThemeType.MOCHA_PARALLEL
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN

buildscript {
    dependencies {
        classpath("org.koin:koin-gradle-plugin:2.1.5")
    }
}

apply(plugin = "koin")
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

    idea
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")

    // Dependency Injection
    implementation("org.koin:koin-core:2.1.5") // ensure koin plugin version is equal

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
    implementation("org.postgresql:postgresql:42.2.12")
    implementation("com.impossibl.pgjdbc-ng:pgjdbc-ng:0.8.4")
    implementation("com.zaxxer:HikariCP:3.2.0")

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

// <WORKARAOUND for="https://github.com/johnrengelman/shadow/issues/448">
project.configurations.implementation.get().isCanBeResolved = true
project.configurations.runtimeOnly.get().isCanBeResolved = true
// </WORKAROUND>

tasks {

    withType<KotlinCompile> {
        kotlinOptions {
            // allWarningsAsErrors = true
            jvmTarget = JavaVersion.VERSION_1_8.toString()
            freeCompilerArgs = listOf(
                "-module-name", project.name,
                "-Xopt-in=kotlin.RequiresOptIn",
                "-Xopt-in=kotlin.contracts.ExperimentalContracts",
                "-Xopt-in=kotlin.time.ExperimentalTime",
                "-Xinline-classes",
                "-Xallow-kotlin-package",
                "-Xallow-result-return-type"
            )
        }
    }

    test {
        useJUnitPlatform()
        maxParallelForks = Runtime.getRuntime().availableProcessors().minus(1).coerceAtLeast(1)
        testlogger {
            theme = MOCHA_PARALLEL
        }
    }

    withType<Jar> {
        archiveBaseName.set(project.name)
    }

    val jarDependencies = register<Jar>("jarDependencies") {
        archiveClassifier.set("dependencies")
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        exclude("META-INF/INDEX.LIST", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "module-info.class")
    }

    val jarApp = register<Jar>("jarApp") {
        archiveClassifier.set("app")
        from(sourceSets.main.get().output)
    }

    jar {
        archiveClassifier.set("all")
        with(jarDependencies.get())
    }

    withType<ShadowJar> {
        archiveBaseName.set(project.name)
        mergeServiceFiles()
//        relocate("org.postgresql.util", "shadow.org.postgresql.util")
        minimize {
//            exclude(dependency("org.jetbrains.exposed:exposed-jdbc"))
        }
        // <WORKARAOUND for="https://github.com/johnrengelman/shadow/issues/448">
        configurations = listOf(
            project.configurations.implementation.get(),
            project.configurations.runtimeOnly.get()
        )
        // </WORKAROUND>
    }

    val shadowJarDependencies = register<ShadowJar>("shadowJarDependencies") {
        archiveClassifier.set("dependencies-shadow")
        configurations = listOf(project.configurations.runtimeClasspath.get())
        exclude("META-INF/INDEX.LIST", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "module-info.class")
    }

    val shadowJarApp = register<ShadowJar>("shadowJarApp") {
        archiveClassifier.set("app-shadow")
        configurations = listOf(project.configurations.runtime.get())
        from(sourceSets.main.get().output)
    }

    shadowJar {
        archiveClassifier.set("all-shadow")
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
