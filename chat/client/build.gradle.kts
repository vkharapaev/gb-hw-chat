plugins {
    application
    id("org.openjfx.javafxplugin") version "0.0.9"
}

javafx {
    version = "11.0.1"
    modules("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation(project(":shared"))
    testImplementation("junit:junit:4.13.1")
}

application {
    mainClass.set("ru.geekbrains.hw.chat.client.ClientApp")
}

description = "client"
