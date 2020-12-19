plugins {
    application
    id("ru.geekbrains.hw.java-conventions")
    id("org.openjfx.javafxplugin") version "0.0.9"
}

javafx {
    version = "15.0.1"
    modules("javafx.controls", "javafx.fxml")
}

application {
    mainClass.set("ru.geekbrains.hw.chat.client.ClientApp")
}

description = "client"
