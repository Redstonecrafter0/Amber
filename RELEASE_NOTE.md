Amber {{ VERSION }}

# Version {{ VERSION }}{{ BUILD_NUMBER }} of Amber is now available.

{{ COMMIT_MESSAGE }}

## Files
- amber-{{ VERSION }}{{ BUILD_NUMBER }}+{{ COMMIT_HASH }}.jar (The Mod's jar)
- amber-{{ VERSION }}{{ BUILD_NUMBER }}+{{ COMMIT_HASH }}-sources.jar (The Mod's sources)

## Dev Setup
Create a fabric mod using [this](https://github.com/SmushyTaco/Example-Mod) repo as an example.  
Add this mod as a dependency via GitHub Packages.
GitHub's Packages requires authentication.
[Here](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry) is their guide.

```kotlin
repositories {
    maven("https://maven.pkg.github.com/Redstonecrafter0/Amber") {
        credentials {
            username = "yourUsername"
            password = "yourGitHubToken"
        }
    }
}

dependencies {
    modImplementation("net.redstonecraft:amber:{{ VERSION }}{{ BUILD_NUMBER }}+{{ COMMIT_HASH }}") {
        exclude("org.lwjgl")
    }
}
```
