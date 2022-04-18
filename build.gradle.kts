import com.adarshr.gradle.testlogger.theme.ThemeType.MOCHA_PARALLEL
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN

plugins {
    val kotlinVersion = "1.6.20"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("com.dorongold.task-tree") version "2.1.0"
    id("com.github.ben-manes.versions") version "0.42.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"

    id("com.adarshr.test-logger") version "3.2.0"

    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"

    jacoco
    id("org.barfuin.gradle.jacocolog") version "2.0.0"

    application

    idea
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlin:kotlin-script-runtime:1.5.0")
    val kotlinxCoroutinesVersion = "1.6.1"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")

    // Dependency Injection
    val koinVersion = "3.2.0-beta-1"
    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")
    implementation("io.insert-koin:koin-ktor:$koinVersion")

    // Cron Dependencies
    implementation("org.quartz-scheduler:quartz:2.3.2")
    implementation("com.cronutils:cron-utils:9.1.6")

    // JSON Dependencies
    implementation("com.google.code.gson:gson:2.9.0")

    val jacksonVersion = "2.13.2"
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")

    // Logging Dependencies
    implementation("io.github.microutils:kotlin-logging:2.1.21")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$kotlinxCoroutinesVersion")
    val log4jVersion = "2.17.2"
    runtimeOnly("org.apache.logging.log4j:log4j-api:$log4jVersion")
    runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
    runtimeOnly("org.apache.logging.log4j:log4j-core:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-api-kotlin:1.1.0") {
        exclude("org.apache.logging.log4j")
    }
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")

    // Database Dependencies
    val exposedVersion = "0.38.1"
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.postgresql:postgresql:42.3.4")
    implementation("com.impossibl.pgjdbc-ng:pgjdbc-ng:0.8.9")
    implementation("com.zaxxer:HikariCP:5.0.1")

    // Test Dependencies
    val junitVersion = "5.8.2"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")

    testImplementation("io.insert-koin:koin-test:$koinVersion")
    testImplementation("io.insert-koin:koin-test-junit5:$koinVersion")

    val kotestVersion = "5.2.3"
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion") // for kotest core jvm assertions
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion") // for kotest framework
    testImplementation("io.kotest:kotest-property-jvm:$kotestVersion") // for kotest property test

    val striktVersion = "0.34.1"
    testImplementation("io.strikt:strikt-core:$striktVersion")
    testImplementation("io.strikt:strikt-jvm:$striktVersion")
    testImplementation("io.strikt:strikt-core:$striktVersion")

    testImplementation("io.mockk:mockk:1.12.3")

    val testContainersVersion = "1.17.1"
    testImplementation("org.testcontainers:testcontainers:$testContainersVersion")
    testImplementation("org.testcontainers:postgresql:$testContainersVersion")
}

// <WORKARAOUND for="https://github.com/johnrengelman/shadow/issues/448">
project.configurations {
    implementation.get().isCanBeResolved = true
    runtimeOnly.get().isCanBeResolved = true
}
// </WORKAROUND>

tasks {

    withType<JavaExec> {
        standardInput = System.`in`
    }

    withType<KotlinCompile> {
        kotlinOptions {
            // allWarningsAsErrors = true
            jvmTarget = "11"
            freeCompilerArgs = listOf(
                "-module-name", project.name,
                "-Xopt-in=kotlin.RequiresOptIn",
                "-Xopt-in=kotlin.contracts.ExperimentalContracts",
                "-Xopt-in=kotlin.time.ExperimentalTime",
                "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi",
                "-Xallow-kotlin-package",
                "-Xallow-result-return-type"
            )
        }
    }

//    withType<Jar> {
//        archiveBaseName.set(project.name)
//    }
//
//    withType<ShadowJar> {
//        archiveBaseName.set(project.name)
//        mergeServiceFiles()
// //        relocate("org.postgresql.util", "shadow.org.postgresql.util")
//        minimize {
// //            exclude(dependency("org.jetbrains.exposed:exposed-jdbc"))
//        }
//        // <WORKARAOUND for="https://github.com/johnrengelman/shadow/issues/448">
//        configurations = listOf(
//            project.configurations.implementation.get(),
//            project.configurations.runtimeOnly.get()
//        )
//        // </WORKAROUND>
//    }
//
//
//    val jarDependencies = register<Jar>("jarDependencies") {
//        archiveClassifier.set("dependencies")
//        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
//        exclude("META-INF/INDEX.LIST", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "module-info.class")
//    }
//
//    val jarApp = register<Jar>("jarApp") {
//        archiveClassifier.set("app")
//        from(sourceSets.main.get().output)
//    }
//
//    jar {
//        archiveClassifier.set("all")
//        with(jarDependencies.get())
//    }
//
//    shadowJar {
//        archiveClassifier.set("shadow")
//        minimize()
//    }

    test {
        useJUnitPlatform()
        maxParallelForks = Runtime.getRuntime().availableProcessors().div(2).coerceAtLeast(1)
    }

    jacocoTestCoverageVerification {
        dependsOn(test, jacocoTestReport)
        violationRules {
            rule {
                limit {
                    minimum = 0.5.toBigDecimal()
                }
            }
        }
    }

    jacocoTestReport {
        dependsOn(test)
        reports {
            xml.isEnabled = true
            html.isEnabled = true
        }
    }

    register("version") {
        println(project.version)
    }

    register("versionSet") {
        val propertyFile = project.file("gradle.properties")
        val property = "version" to project.version
        val propertyFileContent = propertyFile.readText()
        val propertyFileContentNew = propertyFileContent.replace(
            "^${Regex.escape(property.first)}=.*$".toRegex(RegexOption.MULTILINE),
            "${property.first}=${property.second}"
        )
        if (propertyFileContentNew != propertyFileContent) {
            propertyFile.writeText(propertyFileContentNew)
            println("${property.first} set to '${property.second}' in ${propertyFile.relativeTo(projectDir)}")
        }
    }
}

application {
    mainClass.set("me.qoomon.examples.MainKt")
}

testlogger {
    theme = MOCHA_PARALLEL
    showSimpleNames = true
}

ktlint {
//    ignoreFailures.set(true)
    reporters {
        reporter(PLAIN)
        reporter(CHECKSTYLE)
    }
}

jacoco {
    toolVersion = "0.8.5"
    val jacocoExcludes = listOf(
        "de.otto.awsconfigurationmonitor.checkfunction.LambdaEntryPoint"
    )

    tasks.withType<JacocoReportBase> {
        classDirectories.setFrom(
            sourceSets.main.get().output.asFileTree.matching {
                excludes.forEach {
                    it.replace(".", "/")
                        .replace("(?<!\\*\\*)$".toRegex(), ".class")
                        .run { exclude(it) }
                }
            }
        )
    }
}

tasks.withType<JacocoReportBase> {
    val excludes = listOf(
        "org.exmaple.ClassName"
    )

    classDirectories.setFrom(
        sourceSets.main.get().output.asFileTree.matching {
            excludes.flatMap {
                val path = it.replace(".", "/")
                if (path.endsWith("**")) listOf(path)
                else listOf("$path.class", "${path}\$*.class", "${path}Kt.class")
            }.forEach { exclude(it) }
        }
    )
}
