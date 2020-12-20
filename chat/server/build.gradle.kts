plugins {
    application
}

application {
    mainClass.set("ru.geekbrains.hw.chat.server.ServerApp")
}

dependencies {
    implementation(project(":shared"))
    testImplementation("junit:junit:4.13.1")
}

description = "server"
