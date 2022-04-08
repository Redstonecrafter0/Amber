import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("fabric-loom")
    val kotlinVersion: String by System.getProperties()
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka") version "1.6.10"
    id("idea")
    `maven-publish`
}

idea {
    module {
        isDownloadSources = true
    }
}

base {
    val archivesBaseName: String by project
    archivesName.set(archivesBaseName)
}

val modVersion: String by project
version = modVersion
val mavenGroup: String by project
group = mavenGroup

loom {
    accessWidenerPath.set(file("src/main/resources/amber.accesswidener"))
}

repositories {}

dependencies {
    val minecraftVersion: String by project
    minecraft("com.mojang:minecraft:$minecraftVersion")
    val yarnMappings: String by project
    mappings("net.fabricmc:yarn:$yarnMappings:v2")
    val loaderVersion: String by project
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
    val fabricVersion: String by project
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")
    val fabricKotlinVersion: String by project
    modImplementation("net.fabricmc:fabric-language-kotlin:$fabricKotlinVersion")
    val kotlinxSerializationVersion: String by project
    include("org.jetbrains.kotlinx:kotlinx-serialization-json:${kotlinxSerializationVersion}")
}

tasks {
    val javaVersion = JavaVersion.VERSION_17
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
        options.release.set(javaVersion.toString().toInt())
    }
    withType<KotlinCompile> {
        kotlinOptions { jvmTarget = javaVersion.toString() }
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
    }
    jar { from("LICENSE") }
    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") { expand(mutableMapOf("version" to project.version)) }
    }
    java {
        toolchain { languageVersion.set(JavaLanguageVersion.of(javaVersion.toString())) }
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        withSourcesJar()
    }
    dokkaHtml.configure {
        moduleName.set("Amber")
        dokkaSourceSets {
            configureEach {
                includes.from("dokka-docs.md")
                jdkVersion.set(javaVersion.toString().toInt())
            }
        }
    }
}

task("getVersionName") {
    doLast {
        println(project.version)
    }
}

publishing {
    publications {
        create("main", MavenPublication::class) {
            groupId = mavenGroup
            artifactId = "amber"
            version = System.getenv("VERSION_STRING")
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Redstonecrafter0/Amber")
            credentials {
                username = "Redstonecrafter0"
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
