
plugins {
    java
    application
}

group = "sh.gkt"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    testImplementation("info.picocli:picocli:4.7.0")
}

application {
    mainClass.set("sh.gkt.Sudoku")
}

tasks.test {
    useJUnitPlatform()
    maxHeapSize = "1G"
    testLogging {
        events("passed")
    }
}