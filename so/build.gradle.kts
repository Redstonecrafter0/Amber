plugins {
    java
    `maven-publish`

    id("idea")

    alias(libs.plugins.dokka)
    alias(libs.plugins.kotlin)
    id("fabric-loom") version libs.versions.fabric.loom
}

idea {
    module {
        isDownloadSources = true
    }
}

group = property("group")!!
version = property("version")!!
base.archivesName.set(property("archives_base_name")!!.toString() + "-so")

repositories {
    maven("https://maven.fabricmc.net/") {
        name = "Fabric"
    }
    mavenCentral()
}

dependencies {
    minecraft(libs.minecraft)
    mappings(
        variantOf(libs.yarn) {
            classifier("v2")
        }
    )
    modImplementation(libs.bundles.fabric)
}

loom {
    accessWidenerPath.set(file("src/main/resources/so.accesswidener"))
}

val javaVersion = 17

tasks {

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
