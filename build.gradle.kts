@file:Suppress("UnstableApiUsage")

import com.adarshr.gradle.testlogger.theme.ThemeType.MOCHA_PARALLEL
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN

plugins {
    application
    kotlin("jvm") version "1.7.0"; java
    kotlin("plugin.serialization") version "1.7.0"

    id("com.dorongold.task-tree") version "2.1.0"
    id("com.github.ben-manes.versions") version "0.42.0"
    id("com.adarshr.test-logger") version "3.2.0"

    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"

    jacoco
    id("org.barfuin.gradle.jacocolog") version "2.0.0"

    id("com.github.johnrengelman.shadow") version "7.1.2"

    idea
}

kotlin {
    target {
        compilations.all {
            kotlinOptions {
                // allWarningsAsErrors = true
                jvmTarget = JavaVersion.VERSION_17.toString()
                freeCompilerArgs = listOf(
                    "-module-name=${project.name}",
                    "-Xcontext-receivers",
                    "-opt-in=kotlin.contracts.ExperimentalContracts",
                    "-opt-in=kotlin.time.ExperimentalTime",
                    "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
                )
            }
        }
    }
}

repositories { mavenCentral() }

dependencies {
    implementation(kotlin("reflect"))
    implementation(kotlin("script-runtime"))

    val kotlinxCoroutinesVersion = "1.6.2"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")

    // Dependency Injection
    val koinVersion = "3.2.0"
    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")
    implementation("io.insert-koin:koin-ktor:$koinVersion")

    // Cron Dependencies
    implementation("org.quartz-scheduler:quartz:2.3.2")
    implementation("com.cronutils:cron-utils:9.1.6")

    // JSON Dependencies
    implementation("com.google.code.gson:gson:2.9.0")

    val jacksonVersion = "2.13.3"
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")

    // Logging Dependencies
    implementation("io.github.microutils:kotlin-logging:2.1.23")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$kotlinxCoroutinesVersion")
    val log4jVersion = "2.17.2"
    runtimeOnly("org.apache.logging.log4j:log4j-api:$log4jVersion")
    runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
    runtimeOnly("org.apache.logging.log4j:log4j-core:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-api-kotlin:1.1.0") {
        exclude("org.apache.logging.log4j")
    }

    // Database Dependencies
    val exposedVersion = "0.38.2"
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.postgresql:postgresql:42.3.6")
    implementation("com.impossibl.pgjdbc-ng:pgjdbc-ng:0.8.9")
    implementation("com.zaxxer:HikariCP:5.0.1")

    // Test Dependencies
    val junitVersion = "5.8.2"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")

    testImplementation("io.insert-koin:koin-test:$koinVersion")
    testImplementation("io.insert-koin:koin-test-junit5:$koinVersion")

    val kotestVersion = "5.3.0"
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion") // for kotest core jvm assertions
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion") // for kotest framework
    testImplementation("io.kotest:kotest-property-jvm:$kotestVersion") // for kotest property test

    val striktVersion = "0.34.1"
    testImplementation("io.strikt:strikt-core:$striktVersion")
    testImplementation("io.strikt:strikt-jvm:$striktVersion")

    testImplementation("io.mockk:mockk:1.12.4")

    val testContainersVersion = "1.17.2"
    testImplementation("org.testcontainers:testcontainers:$testContainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")
    testImplementation("org.testcontainers:postgresql:$testContainersVersion")

    testImplementation("com.tngtech.archunit:archunit-junit5:0.23.1")
}

application {
    mainClass.set("me.qoomon.examples.MainKt")
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()

            sources {
                java {
                    exclude("**/*IT.kt")
                }
            }

            targets {
                all {
                    testTask.configure {
                        // set a system property for the test JVM(s)
                        systemProperty("some.prop", "value")
                        options {
                            maxParallelForks = Runtime.getRuntime().availableProcessors().coerceAtLeast(1)
                        }
                    }
                }
            }
        }

        val integrationTest by registering(JvmTestSuite::class) {
            useJUnitJupiter()

            dependencies {
                implementation(project)
                // add test dependencies (optiona, if not needed)
                configurations.testImplementation {
                    dependencies.forEach(::implementation)
                }
            }

            sources {
                java {
                    setSrcDirs(
                        listOf(
                            "src/main/kotlin",
                            "src/test/kotlin",
                        )
                    )
                    include("**/*IT.kt")
                }
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                    }
                }
            }
        }
    }
}

testlogger {
    theme = MOCHA_PARALLEL
    showSimpleNames = true
}

jacoco {
    toolVersion = "0.8.8"
    exclude(
        "me.qoomon.demo.a.Base",
    )
}

ktlint {
//    ignoreFailures.set(true)
    version.set("0.45.2")
    enableExperimentalRules.set(true)
    reporters {
        reporter(PLAIN)
        reporter(CHECKSTYLE)
    }
}

tasks {

    processResources {
        filesMatching("application.properties") {
            // groovy template engine($ { placholder })
            expand(project.properties)
            expand("version" to project.version)

//            // groovy template engine (@placholder@)
//            filter<ReplaceTokens>(
//                "tokens" to mapOf(
//                    "version" to project.version
//                )
//            )
        }
    }

    withType<JavaExec> {
        standardInput = System.`in`
    }

    withType<JavaExec>().configureEach {
    }

    jar {
        archiveBaseName.set(project.name)
    }

    shadowJar {
        archiveBaseName.set(project.name)
        mergeServiceFiles()
        // relocate("org.postgresql.util", "shadow.org.postgresql.util")
        minimize {
            // exclude(dependency("org.jetbrains.exposed:exposed-jdbc"))
        }
        // <WORKAROUND for="https://github.com/johnrengelman/shadow/issues/448">
        configurations = listOf(
            project.configurations.implementation.get(),
            project.configurations.runtimeOnly.get()
        ).onEach { it.isCanBeResolved = true }
        // </WORKAROUND>
    }

    withType<Test>().configureEach {
        useJUnitPlatform()
        // exclude("**/*IT.class")
    }

    // test {
    //     useJUnitPlatform()
    // }
    //
    // register<Test>("integrationTest") {
    //     group = VERIFICATION_GROUP
    //     description = "Runs the integration test suite."
    //     useJUnitPlatform()
    //     include("**/*IT.class")
    // }

    check {
        dependsOn(testing.suites)
        dependsOn(jacocoTestReport)
        dependsOn(jacocoTestCoverageVerification)
    }

    jacocoTestReport {
    }

    jacocoTestCoverageVerification {
        violationRules {
            rule {
                element
                limit {
                    minimum = 0.5.toBigDecimal()
                }
            }
        }
    }

    register<Task>("version") {
        println(project.version)
    }

    register<Task>("versionSet") {
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

// --- Helper Functions ------------------------------------------------------------------------------------------------

fun JacocoPluginExtension.exclude(vararg excludeRefs: String) {
    tasks.withType<JacocoReportBase>().configureEach { exclude(*excludeRefs) }
}

fun JacocoReportBase.exclude(vararg excludeRefs: String) {
    val excludePaths = excludeRefs
        .map { it.replace(".", "/") }
        .flatMap {
            listOf(
                "$it.class", "$it\$*.class", // java classes
                "${it}Kt.class", "${it}Kt\$*.class", // kotlin classes
            )
        }
    this.classDirectories.setFrom(files(this.classDirectories.files.map { fileTree(it) { exclude(excludePaths) } }))
}
