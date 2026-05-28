plugins {
    id("java")
    id("antlr")
}

group = "net.aros.chimera"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations:26.0.2")
    implementation("it.unimi.dsi:fastutil:8.5.18")

    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    "antlr"("org.antlr:antlr4:4.7.1")
}

tasks.generateGrammarSource {
    arguments = arguments + listOf("-visitor")
}

tasks.test {
    useJUnitPlatform()
}