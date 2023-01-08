import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    java

    // https://docs.gradle.org/current/userguide/application_plugin.html
    application

    // https://docs.gradle.org/current/userguide/distribution_plugin.html
    distribution
}

group = "sh.gkt"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Unit Testing
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

    // We use Awesome Java to choose helpful libraries: https://github.com/akullpp/awesome-java
    // Command-Line Interface
    implementation("info.picocli:picocli:4.7.0")

    // Lightweight logging
    implementation("org.tinylog:tinylog-api:2.5.0")
    implementation("org.tinylog:tinylog-impl:2.5.0")

    // Console I/O (not using this yet)
    implementation("org.jline:jline:3.21.0")
}

application {
    mainClass.set("sh.gkt.SudokuUX")
}

tasks {
    test {
        useJUnitPlatform()
        maxHeapSize = "1G"
        testLogging {
            events(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
        }
    }
}
