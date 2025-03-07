plugins {
	// Apply the shared build logic from a convention plugin.
	// The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
	id("buildsrc.convention.kotlin-jvm")
	id("java")
	id("java-library")
	groovy
	// Apply Kotlin Serialization plugin from `gradle/libs.versions.toml`.
	alias(libs.plugins.kotlinPluginSerialization)
}

dependencies {
	// Apply the kotlinx bundle of dependencies from the version catalog (`gradle/libs.versions.toml`).
	implementation(libs.bundles.kotlinxEcosystem)
	testImplementation(kotlin("test"))

	// https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
	implementation("ch.qos.logback:logback-classic:1.5.17")
	// https://mvnrepository.com/artifact/ch.qos.logback/logback-core
	implementation("ch.qos.logback:logback-core:1.5.17")
	// https://mvnrepository.com/artifact/org.slf4j/slf4j-api
	implementation("org.slf4j:slf4j-api:2.1.0-alpha1")

	//gson
	implementation("com.google.code.gson:gson:2.10.1")

	implementation("com.sleepycat:je:18.3.12")

	// HikariCP
	implementation("com.zaxxer:HikariCP:5.0.1")

	//h2
	implementation("com.h2database:h2:2.2.220")

	//jna
	implementation("net.java.dev.jna:jna:5.12.1")

	// lombok
	// https://mvnrepository.com/artifact/org.projectlombok/lombok
	compileOnly("org.projectlombok:lombok:1.18.36")

	// common-codec
	// https://mvnrepository.com/artifact/commons-codec/commons-codec
	implementation("commons-codec:commons-codec:1.18.0")

	//crypto
	implementation("org.bouncycastle:bcprov-jdk18on:1.78")
	implementation("org.bouncycastle:bcpkix-jdk18on:1.78")

	// https://mvnrepository.com/artifact/org.mapdb/mapdb
	implementation("org.mapdb:mapdb:3.1.0")

}

tasks.register("runMaster",JavaExec::class){
	group = "run"
	description = "Run Master"

	mainClass = "ice.myrtle.citrus.Master"

	classpath = sourceSets.main.get().runtimeClasspath

	args = listOf()

	jvmArgs = listOf("-Xms2048m", "-Xmx1204m")
}
