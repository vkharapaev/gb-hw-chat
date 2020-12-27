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

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

application {
    mainClass.set("ru.geekbrains.hw.chat.client.ClientApp")
}

tasks.test {
    useJUnitPlatform()
}

description = "client"
