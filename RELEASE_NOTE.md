Amber {{ VERSION }}

# Version {{ VERSION }}{{ BUILD_NUMBER }} of Amber is now available.

{{ COMMIT_MESSAGE }}

## Files
- amber-{{ VERSION }}{{ BUILD_NUMBER }}+{{ COMMIT_HASH }}.jar (The Mod's jar)
- amber-{{ VERSION }}{{ BUILD_NUMBER }}+{{ COMMIT_HASH }}-sources.jar (The Mod's sources)

## Dev Setup
Create a fabric mod using [this](https://github.com/SmushyTaco/Example-Mod) repo as an example.  
Add this mod as a dependency.
```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    modImplementation("com.github.Redstonecrafter0:Amber:{{ VERSION }}{{ BUILD_NUMBER }}+{{ COMMIT_HASH }}")
}
```
