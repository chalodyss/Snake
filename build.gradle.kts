group   = "abitodyssey.snake"
version = "1.0.0"

plugins {
    application
    id("org.javamodularity.moduleplugin") version "1.8.15"
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.beryx.jlink") version "3.0.0"
}

repositories {
    mavenCentral()
}

application {
    mainClass   = "abitodyssey.snake.Main"
    mainModule  = "snake"
}

javafx {
    modules("javafx.graphics", "javafx.controls", "javafx.fxml")
}

tasks.withType<Jar> {
    archiveBaseName = "Snake"

    manifest {
        attributes["Main-Class"] = "abitodyssey.snake.Main"
    }
}

jlink {
    options = listOf("--strip-debug", "--no-header-files", "--no-man-pages", "--bind-services")

    launcher {
        name = "snake"
    }
}
