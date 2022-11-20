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
}

javafx {
	version = "17"
	modules = listOf("javafx.controls")
}

application.mainClass.set("project.Main")
