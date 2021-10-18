import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.3.4.RELEASE"
	id("io.spring.dependency-management") version "1.0.10.RELEASE"
	kotlin("jvm") version "1.5.10"
	kotlin("plugin.spring") version "1.5.10"
	id("groovy")
	id("jacoco")
	id("org.sonarqube") version "2.8"
	id("com.gorylenko.gradle-git-properties") version "2.2.3"
}

val app = mapOf (
  "group" to "com.prismamp.todopago",
  "name" to "ms-acquirer-pos",
  "version" to "0.1.0"
)

group = app["group"] ?: error("")
version = app["version"] ?: error("")
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	jcenter()
	mavenCentral()
	maven {
		url = uri("http://nexus.redb.ee/content/repositories/redbee-snapshot/")
		credentials {
			username = "usernameNexusRedBee"
			password = "passwordNexusRedBee"
		}
	}
	maven {
		url = uri("http://nexus.redb.ee/content/repositories/redbee-release/")
		credentials {
			username = "usernameNexusRedBee"
			password = "passwordNexusRedBee"
		}
	}
}

extra["springCloudVersion"] = "Hoxton.SR11"
extra["logstashLogbackEncoderVersion"] = "6.4"
extra["swaggerVersion"] = "3.0.0"
extra["spekVersion"] = "2.0.9"
extra["mockkVersion"] = "1.10.0"

springBoot {
	buildInfo()
}

jacoco {
	toolVersion = "0.8.7"
}

tasks.jacocoTestReport {
	reports {
		xml.isEnabled = true
		html.isEnabled = false
		csv.isEnabled = false
		xml.destination = file("${buildDir}/jacoco/jacoco.xml")
	}
}

tasks.test {
	extensions.configure(JacocoTaskExtension::class) {
		setDestinationFile(file("$buildDir/jacoco/jacocoTest.exec"))
		classDumpDir = file("${buildDir}/jacoco/classpathdumps")
	}
	finalizedBy(tasks.jacocoTestReport)

	testLogging {
		exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
		events("PASSED", "FAILED", "SKIPPED")

	}
}

sonarqube {
	properties {
		property("sonar.coverage.jacoco.xmlReportPaths", "${buildDir}/jacoco/jacoco.xml")
		property(
			"sonar.coverage.exclusions",
			"src/main/kotlin/com/prismamp/todopago/util/**, " +
					"src/main/kotlin/com/prismamp/todopago/configuration/**, " +
					"src/main/kotlin/com/prismamp/todopago/payment/**/model/**"
		)
	}
}

val commonsSecurityVersion = "3.1.3-RELEASE"
val commonsCacheVersion = "2.1.0-RELEASE"
val commonsRestVersion = "1.10.0-RELEASE"
val commonsTenantVersion = "1.4.0-RELEASE"
val commonsQueuesVersion = "0.1.4-RELEASE"
val commonsStorageVersion= "1.3.1-RELEASE"
val arrowVersion = "0.13.2"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.cloud:spring-cloud-starter-config")
	implementation("org.springframework.cloud:spring-cloud-starter-sleuth")
	implementation("org.springframework.retry:spring-retry")

	implementation("io.springfox:springfox-boot-starter:${property("swaggerVersion")}")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

	implementation("net.logstash.logback:logstash-logback-encoder:${property("logstashLogbackEncoderVersion")}")

	implementation("javax.xml.bind:jaxb-api:2.2.12")
	implementation("javax.activation:activation:1.1")

	implementation("com.prismamp.todopago:commons-tenant:$commonsTenantVersion")
	implementation("com.prismamp.todopago:commons-rest:$commonsRestVersion")
	implementation("com.prismamp.todopago:commons-cache:$commonsCacheVersion")
	implementation("com.prismamp.todopago:commons-security:$commonsSecurityVersion")
	implementation("com.prismamp.todopago:commons-queues:$commonsQueuesVersion")
	implementation("com.prismamp.todopago:commons-storage:$commonsStorageVersion")


	implementation ("io.arrow-kt:arrow-fx-coroutines:$arrowVersion")

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
	testImplementation("org.jetbrains.kotlin:kotlin-test")
	testImplementation("org.spekframework.spek2:spek-dsl-jvm:${property("spekVersion")}")
	testImplementation("org.spekframework.spek2:spek-runner-junit5:${property("spekVersion")}")
	testImplementation("io.mockk:mockk:${property("mockkVersion")}")
	testImplementation("com.tngtech.archunit:archunit:0.18.0")
	testImplementation("com.tngtech.archunit:archunit-junit5-engine:0.13.0")
	testImplementation("com.winterbe:expekt:0.5.0")
	testImplementation("io.strikt:strikt-core:0.24.0")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.withType<Test> {
	useJUnitPlatform {
		includeEngines("spek2")
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}
