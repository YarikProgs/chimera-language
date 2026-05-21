plugins {
    java
    antlr
    application
}

group = "net.aros.chimera"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    "antlr"("org.antlr:antlr4:4.7.1")
    implementation("org.antlr:antlr4-runtime:4.7.1")
}

application {
    mainClass.set("net.aros.chimera.ChimeraMain")
}

tasks.generateGrammarSource {
    arguments = arguments + listOf("-visitor")
}

tasks.test {
    useJUnitPlatform()
}