import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.6.7"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
}

group = "com.linkpreview"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.junit.jupiter:junit-jupiter:5.8.2")
	implementation("org.testng:testng:7.5")
	implementation("io.kotest:kotest-runner-junit5-jvm:5.3.0")

	// Parser HTML
	implementation("org.jsoup:jsoup:1.11.3")

	testImplementation("io.cucumber:cucumber-java:7.3.3")
	testImplementation("io.cucumber:cucumber-junit:7.3.3")

	// Validate URL
	implementation("commons-validator:commons-validator:1.7")

	// Selenium WebDrive
	implementation("org.seleniumhq.selenium:selenium-java:3.141.59")

	// Chrome Driver for Selenium
	implementation("org.seleniumhq.selenium:selenium-chrome-driver:3.141.59")

	// Beautiful Logger
	implementation("io.github.microutils:kotlin-logging-jvm:2.1.21")

	// Swagger
	implementation("org.springdoc:springdoc-openapi-data-rest:1.6.0")
	implementation("org.springdoc:springdoc-openapi-ui:1.6.6")
	implementation("org.springdoc:springdoc-openapi-kotlin:1.6.0")

	// Tests
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
	testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
	testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
	testImplementation("io.mockk:mockk:1.12.3")
	testImplementation("org.springframework.boot:spring-boot-starter-test:2.6.5")
	testImplementation("com.ninja-squad:springmockk:3.1.1")
	testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.0")

	testImplementation("com.ninja-squad:springmockk:3.1.1")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.0")
	testImplementation("io.kotest:kotest-runner-junit5-jvm:5.1.0")
	testImplementation("io.mockk:mockk:1.12.3")
	testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.10")

	implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-native-mt")

	implementation("org.springframework.boot:spring-boot-starter-web:2.6.5")
	implementation("org.springframework.data:spring-data-jdbc:2.3.3")


	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<Test> {
	this.testLogging {
		this.showStandardStreams = true
	}
}