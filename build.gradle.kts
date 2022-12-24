plugins {
	id("java")
	id("application")
	id("org.openjfx.javafxplugin") version "0.0.13"
}

group = "project"
version = "0.0.1"

repositories {
	mavenCentral()
}

dependencies {
    implementation( group = "org.fxmisc.richtext", name = "richtextfx", version = "0.11.0")
}

javafx {
	version = "17"
	modules = listOf("javafx.controls", "javafx.fxml")
}

application.mainClass.set("project.Main")
