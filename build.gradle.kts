import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	java
	`maven-publish`

	id("org.jetbrains.dokka") version "1.6.10"
	id("idea")

	alias(libs.plugins.kotlin)
	alias(libs.plugins.quilt.loom)
	alias(libs.plugins.kotlinx.serialization)
}

idea {
	module {
		isDownloadSources = true
	}
}

val archives_base_name: String by project
base.archivesName.set(archives_base_name)

loom {
	accessWidenerPath.set(file("src/main/resources/amber.accesswidener"))
}

val javaVersion = 17

repositories {
	mavenCentral()
}

dependencies {
	minecraft(libs.minecraft)
	mappings(
		variantOf(libs.quilt.mappings) {
			classifier("intermediary-v2")
		}
	)

	implementation(libs.kotlinx.serialization.json)

	modImplementation(libs.quilt.loader)

	// QSL is not a complete API; You will need Quilted Fabric API to fill in the gaps.
	// Quilted Fabric API will automatically pull in the correct QSL version.
	modImplementation(libs.qfapi)

	modImplementation(libs.qkl)
}

tasks {
	withType<KotlinCompile> {
		kotlinOptions {
			jvmTarget = javaVersion.toString()
			languageVersion = libs.plugins.kotlin.get().version.requiredVersion.substringBeforeLast('.')
		}
	}

	withType<JavaCompile>().configureEach {
		options.encoding = "UTF-8"
		options.isDeprecation = true
		options.release.set(javaVersion)
	}

	processResources {
		filteringCharset = "UTF-8"
		inputs.property("version", project.version)

		filesMatching("quilt.mod.json") {
			expand(
				mapOf(
					"version" to project.version
				)
			)
		}
	}

	wrapper {
		distributionType = Wrapper.DistributionType.BIN
	}

	jar {
		from("LICENSE") {
			rename { "LICENSE_${archives_base_name}" }
		}
	}

	dokkaHtml.configure {
		moduleName.set("Amber")
		dokkaSourceSets {
			configureEach {
				includes.from("dokka-docs.md")
				jdkVersion.set(javaVersion)
			}
		}
	}
}

sourceSets {
	main {
		java.setSrcDirs(
			if (System.getenv("TARGET") == "core") {
				listOf("src/main/kotlin", "src/main/java")
			} else {
				listOf("src/main/kotlin", "src/main/java", "src/common/kotlin", "src/common/java")
			}.map { file(it) }
		)
	}
}

val targetJavaVersion = JavaVersion.toVersion(javaVersion)
if (JavaVersion.current() < targetJavaVersion) {
	kotlin.jvmToolchain(javaVersion)

	java.toolchain {
		languageVersion.set(JavaLanguageVersion.of(javaVersion))
	}
}

java {
	withSourcesJar()

	sourceCompatibility = targetJavaVersion
	targetCompatibility = targetJavaVersion
}

publishing {
	publications {
		register<MavenPublication>("Maven") {
			from(components.getByName("java"))
		}
	}

	// See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
	repositories {
	}
}
