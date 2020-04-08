import com.adarshr.gradle.testlogger.theme.ThemeType.MOCHA_PARALLEL
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN

plugins {
    val kotlinVersion = "1.3.71"
    kotlin("jvm") version kotlinVersion
//    kotlin("kapt") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("com.adarshr.test-logger") version "2.0.0"

    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {

    implementation(kotlin("stdlib-jdk8"))
    // implementation(kotlin("reflect"))
    // implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")

    implementation("com.amazonaws:aws-lambda-java-core:1.2.0")
    implementation("com.amazonaws:aws-lambda-java-events:2.2.7")

    implementation(platform("software.amazon.awssdk:bom:2.5.29"))
    implementation("software.amazon.awssdk:iam")
    implementation("software.amazon.awssdk:ec2")
    implementation("software.amazon.awssdk:s3")

    // Logging Dependencies
    implementation("io.github.microutils:kotlin-logging:1.7.9")
    runtimeOnly("org.slf4j:slf4j-api:1.7.30")
    val log4jVersion = "2.13.1"
    runtimeOnly("org.apache.logging.log4j:log4j-api:$log4jVersion")
    runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
    runtimeOnly("com.amazonaws:aws-lambda-java-log4j2:1.0.0")

    // Test Dependencies
    val junitVersion = "5.6.1"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("io.strikt:strikt-core:0.24.0")
    testImplementation("io.mockk:mockk:1.9.3")
    testRuntimeOnly("net.bytebuddy:byte-buddy:1.10.9") // WORKAROUND https://github.com/mockk/mockk/issues/397
}

val jvmTargetVersion = JavaVersion.VERSION_11.toString()

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
            jvmTarget = jvmTargetVersion
            sourceCompatibility = jvmTargetVersion
        }
    }

    withType<ShadowJar> {
        archiveBaseName.set(project.tasks.jar.get().archiveBaseName)
        mergeServiceFiles()
        minimize()
    }
}

ktlint {
    ignoreFailures.set(true)
    reporters {
        reporter(PLAIN)
        reporter(CHECKSTYLE)
    }
}
