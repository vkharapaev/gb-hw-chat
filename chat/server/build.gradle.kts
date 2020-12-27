plugins {
    application
    id("io.freefair.lombok") version "5.3.0"
}

application {
    mainClass.set("ru.geekbrains.hw.chat.server.ServerApp")
}

dependencies {
    implementation(project(":shared"))
    implementation("org.xerial:sqlite-jdbc:3.34.0")

    compileOnly("org.projectlombok:lombok:1.18.16")
    annotationProcessor("org.projectlombok:lombok:1.18.16")
    testCompileOnly("org.projectlombok:lombok:1.18.16")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.16")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

tasks.test {
    useJUnitPlatform()
}

description = "server"
