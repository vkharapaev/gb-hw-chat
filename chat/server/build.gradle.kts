plugins {
    application
    id("io.freefair.lombok") version "5.3.0"
}

application {
    mainClass.set("ru.geekbrains.hw.chat.server.ServerApp")
    applicationDefaultJvmArgs = listOf("-Dlog4j.configuration=file:log4j.xml")
}

dependencies {
    implementation(project(":shared"))
    implementation("org.xerial:sqlite-jdbc:3.34.0")
    implementation("log4j:log4j:1.2.17")

    compileOnly("org.projectlombok:lombok:1.18.16")
    annotationProcessor("org.projectlombok:lombok:1.18.16")
    testCompileOnly("org.projectlombok:lombok:1.18.16")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.16")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")

    testImplementation("org.mockito:mockito-core:3.6.28")
    testImplementation("org.mockito:mockito-junit-jupiter:3.6.28")
}

tasks.test {
    useJUnitPlatform()
}

description = "server"
