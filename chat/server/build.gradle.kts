plugins {
    application
}

application {
    mainClass.set("ru.geekbrains.hw.chat.server.ServerApp")
}

dependencies {
    implementation(project(":shared"))
    implementation("org.xerial:sqlite-jdbc:3.34.0")
    testImplementation("junit:junit:4.13.1")
}

description = "server"
