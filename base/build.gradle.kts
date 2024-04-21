plugins {
    java
    `maven-publish`

    id("idea")

    alias(libs.plugins.dokka)
    alias(libs.plugins.kotlin)
    id("fabric-loom") version libs.versions.fabric.loom
    alias(libs.plugins.kotlinx.serialization)
}

idea {
    module {
        isDownloadSources = true
    }
}

group = property("group")!!
version = property("version")!!
base.archivesName.set(property("archives_base_name")!!.toString() + "-base")

repositories {
    maven("https://maven.fabricmc.net/") {
        name = "Fabric"
    }
    mavenCentral()
}

dependencies {
    minecraft(libs.minecraft)
    mappings(libs.yarn)
    modImplementation(libs.bundles.fabric)
    implementation(libs.bundles.lwjgl)
}

loom {
    accessWidenerPath.set(file("src/main/resources/amber.accesswidener"))
}

val javaVersion = 17

tasks {

    processResources {
        filteringCharset = "UTF-8"
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") {
            expand(getProperties())
        }
    }

    jar {
        from("LICENSE")
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

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                artifact(remapJar) {
                    builtBy(remapJar)
                }
                artifact(kotlinSourcesJar) {
                    builtBy(remapSourcesJar)
                }
            }
        }

        repositories {
        }
    }

    compileKotlin {
        kotlinOptions.jvmTarget = javaVersion.toString()
    }

}

java {
    withSourcesJar()
}
