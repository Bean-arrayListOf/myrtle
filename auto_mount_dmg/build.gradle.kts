plugins {
	// Apply the shared build logic from a convention plugin.
	// The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
	id("buildsrc.convention.kotlin-jvm")

	// Apply the Application plugin to add support for building an executable JVM application.
	application
}

dependencies {
	// Project "app" depends on project "utils". (Project paths are separated with ":", so ":utils" refers to the top-level "utils" project.)
	implementation(project(":citrus"))

	// https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
	implementation("ch.qos.logback:logback-classic:1.5.17")
	// https://mvnrepository.com/artifact/ch.qos.logback/logback-core
	implementation("ch.qos.logback:logback-core:1.5.17")
	// https://mvnrepository.com/artifact/org.slf4j/slf4j-api
	implementation("org.slf4j:slf4j-api:2.1.0-alpha1")

	implementation("com.google.code.gson:gson:2.10.1")

	implementation("org.mapdb:mapdb:3.1.0")

	implementation("net.java.dev.jna:jna:5.12.1")
}

application {
	// Define the Fully Qualified Name for the application main class
	// (Note that Kotlin compiles `App.kt` to a class with FQN `com.example.app.AppKt`.)
	mainClass = "org.myrtle.app.AppKt"
}

tasks.jar {
	manifest{
		attributes["Main-Class"] = "ice.myrtle.message.Master"
	}
}

tasks.register("runMaster",JavaExec::class.java) {
	group = "runApplication"
	description = "Run the master application"
	classpath = sourceSets["main"].runtimeClasspath
	mainClass.set("ice.myrtle.message.Master")
	args = listOf<String>()
	jvmArgs = listOf()
}
