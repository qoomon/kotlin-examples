@file:Suppress("UnstableApiUsage")

import com.adarshr.gradle.testlogger.theme.ThemeType.MOCHA_PARALLEL

plugins {
    application

    val kotlinVersion = "1.9.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("com.dorongold.task-tree") version "2.1.1"
    id("com.github.ben-manes.versions") version "0.47.0"
    id("com.adarshr.test-logger") version "3.2.0"

    id("io.gitlab.arturbosch.detekt").version("1.23.1")
//  TODO   id("org.jlleitschuh.gradle.ktlint") version "11.5.1"

    jacoco
    id("org.barfuin.gradle.jacocolog") version "3.1.0"

    id("com.github.johnrengelman.shadow") version "8.1.1"

    idea
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    implementation(kotlin("script-runtime"))

    val kotlinxCoroutinesVersion = "1.7.3"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

    // Dependency Injection
    val koinVersion = "3.4.3"
    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")
    implementation("io.insert-koin:koin-ktor:$koinVersion")

    // Cron Dependencies
    implementation("org.quartz-scheduler:quartz:2.5.0-rc1")
    implementation("com.cronutils:cron-utils:9.2.1")

    // JSON Dependencies
    implementation("com.google.code.gson:gson:2.10.1")

    val jacksonVersion = "2.15.2"
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")

    // Logging Dependencies
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$kotlinxCoroutinesVersion")
    val log4jVersion = "3.0.0-alpha1"
    runtimeOnly("org.apache.logging.log4j:log4j-api:$log4jVersion")
    runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
    runtimeOnly("org.apache.logging.log4j:log4j-core:$log4jVersion")

    // Database Dependencies
    val exposedVersion = "0.42.1"
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-json:$exposedVersion")
    implementation("org.postgresql:postgresql:42.6.0")
    implementation("com.impossibl.pgjdbc-ng:pgjdbc-ng:0.8.9")
    implementation("com.zaxxer:HikariCP:5.0.1")

    // Test Dependencies
    testImplementation(kotlin("test-junit5"))
//    val junitVersion = "5.10.0"
//    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
//    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
//    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
//    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
//    testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitVersion")

    val kotestVersion = "5.6.2"
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-property-jvm:$kotestVersion")

    val striktVersion = "0.34.1"
    testImplementation("io.strikt:strikt-core:$striktVersion")
    testImplementation("io.strikt:strikt-jvm:$striktVersion")

    testImplementation("io.mockk:mockk:1.13.7")

    val testContainersVersion = "1.19.0"
    testImplementation("org.testcontainers:testcontainers:$testContainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")
    testImplementation("org.testcontainers:postgresql:$testContainersVersion")

    testImplementation("com.tngtech.archunit:archunit-junit5:1.1.0")

    testImplementation("io.insert-koin:koin-test-junit5:$koinVersion") {
        exclude("org.jetbrains.kotlin", "kotlin-test-junit")
    }
}

application {
    mainClass.set("me.qoomon.examples.MainKt")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

kotlin {
    jvmToolchain {
//        languageVersion.set(JavaLanguageVersion.of(20))
    }
    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_9)
        moduleName = project.name
        optIn.add("kotlin.contracts.ExperimentalContracts")
        optIn.add("kotlin.time.ExperimentalTime")
        optIn.add("kotlinx.serialization.ExperimentalSerializationApi")
        freeCompilerArgs.add("-Xcontext-receivers")
    }
}

tasks.processResources {
    filesMatching("application.properties") {
        // groovy template engine($ { placeholder })
        expand(project.properties)
        expand("version" to project.version)
//            // groovy template engine (@placeholder@)
//            filter<ReplaceTokens>(
//                "tokens" to mapOf(
//                    "version" to project.version
//                )
//            )
    }
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
                implementation(project())
                // add test dependencies (optional, if not needed)
                configurations.testImplementation {
                    dependencies.forEach { implementation(it) }
                }
            }

            sources {
                java {
                    setSrcDirs(
                        listOf(
                            "src/main/kotlin",
                            "src/test/kotlin",
                        ),
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

tasks.test {
    useJUnitPlatform()
    // exclude("**/*IT.class")
}
//tasks.register<Test>("integrationTest") {
//     group = LifecycleBasePlugin.VERIFICATION_GROUP
//     description = "Runs the integration test suite."
//     useJUnitPlatform()
//     include("**/*IT.class")
// }

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
tasks.jacocoTestReport {
}
tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            element = "BUNDLE"
            limit {
                counter = "INSTRUCTION"
                value = "COVEREDRATIO"
                minimum = 0.1.toBigDecimal()
            }
        }
    }
}

//ktlint {
////    ignoreFailures.set(true)
//    version = "0.50.0"
//    enableExperimentalRules = true
//    reporters {
//        reporter(PLAIN)
//        reporter(CHECKSTYLE)
//    }
//}

tasks.shadowJar {
    // archiveVersion = ""
    // archiveClassifier = ""
    mergeServiceFiles()
    // relocate("org.postgresql.util", "shadow.org.postgresql.util")
    minimize {
        // exclude(dependency("org.jetbrains.exposed:exposed-jdbc"))
    }
}

tasks.check {
    dependsOn(testing.suites)
    dependsOn(tasks.jacocoTestReport)
    dependsOn(tasks.jacocoTestCoverageVerification)
}

// get version ./gradlew version
tasks.register<Task>("version") {
    doLast {
        logger.lifecycle(project.version.toString())
    }
}
// set version ./gradlew version.set -Pversion=1.2.3
tasks.register<Task>("version.set") {
    doLast {
        val propertyFile = project.file("gradle.properties")
        val property = "version" to project.version
        val propertyLineRegex = "^${Regex.escape(property.first)}=.*$".toRegex(RegexOption.MULTILINE)
        val propertyFileContent = propertyFile.readText()

        val propertyFileContentNew = if (propertyFileContent.contains(propertyLineRegex)) {
            propertyFileContent.replace(propertyLineRegex, "${property.first}=${property.second}")
        } else {
            propertyFileContent + "\n" + "${property.first}=${property.second}"
        }
        if (propertyFileContentNew != propertyFileContent) {
            propertyFile.writeText(propertyFileContentNew)
        }
        logger.lifecycle("Property ${property.first} set to '${property.second}' in ${propertyFile.relativeTo(projectDir)}")
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
                "$it.class",
                "$it\$*.class", // java classes
                "${it}Kt.class",
                "${it}Kt\$*.class", // kotlin classes
            )
        }
    this.classDirectories.setFrom(files(this.classDirectories.files.map { fileTree(it) { exclude(excludePaths) } }))
}
