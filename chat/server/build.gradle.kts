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
    testImplementation("junit:junit:4.13.1")

    compileOnly("org.projectlombok:lombok:1.18.16")
    annotationProcessor("org.projectlombok:lombok:1.18.16")
    testCompileOnly("org.projectlombok:lombok:1.18.16")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.16")
}

description = "server"
