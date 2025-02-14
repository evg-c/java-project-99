plugins {
	java
	application
	checkstyle
	jacoco
	id("org.springframework.boot") version "3.3.8"
	id("io.spring.dependency-management") version "1.1.7"
}

application {
	mainClass.set("hexlet.code.app.AppApplication")
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.jacocoTestReport {
	reports {
		xml.required = true
	}
}