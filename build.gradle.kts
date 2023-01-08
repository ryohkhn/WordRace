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
	implementation(
		group = "org.fxmisc.richtext",
		name = "richtextfx",
		version = "0.11.0"
	)
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

javafx {
	version = "17"
	modules = listOf("javafx.controls", "javafx.fxml")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

application.mainClass.set("project.Main")
