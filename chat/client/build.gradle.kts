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
    implementation("io.reactivex.rxjava2:rxjava:2.2.20")
    implementation("io.reactivex:rxjavafx:2.0.2")
    testImplementation("junit:junit:4.13.1")
}

application {
    mainClass.set("ru.geekbrains.hw.chat.client.ClientApp")
}

description = "client"
