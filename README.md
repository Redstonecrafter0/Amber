<div align="center">
    <h1>Amber</h1>
    <p>A fabric modding base for the latest Minecraft version.</p>
    <img src="https://img.shields.io/github/workflow/status/Redstonecrafter0/Amber/Pipeline?logo=github-actions&style=for-the-badge" alt="BUILD">
    <br>
    <img src="https://img.shields.io/github/v/release/Redstonecrafter0/Amber?logo=github&style=for-the-badge" alt="RELEASE">
    <img src="https://img.shields.io/github/release-date/Redstonecrafter0/Amber?style=for-the-badge&logo=github" alt="RELEASE DATE">
    <br>
    <img src="https://img.shields.io/github/v/release/Redstonecrafter0/Amber?include_prereleases&label=pre-release&logo=github&style=for-the-badge" alt="PRE-RELEASE">
    <img src="https://img.shields.io/github/release-date-pre/Redstonecrafter0/Amber?label=pre-release%20date&style=for-the-badge&logo=github" alt="PRE-RELEASE DATE">
    <br>
    <img src="https://img.shields.io/github/repo-size/Redstonecrafter0/Amber?style=for-the-badge" alt="REPO SIZE">
    <img src="https://img.shields.io/github/license/Redstonecrafter0/Amber?style=for-the-badge" alt="LICENSE">
    <br>
    <img src="https://img.shields.io/github/issues/Redstonecrafter0/Amber?style=for-the-badge" alt="Open Issues">
</div>

# Setup
Create a fabric mod using [this](https://github.com/SmushyTaco/Example-Mod) repo as an example.

Add this mod as a dependency via GitHub Packages.
GitHub's Packages requires authentication.
[Here](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry) is their guide.
Use the release tag as the version.

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
    modImplementation("net.redstonecraft:amber:${version}")
}
```

## Documentation
The documentation can be found [here](https://redstonecrafter0.github.io/Amber/dokka).

## Code Scanning
The latest Qodana report can be found [here](https://redstonecrafter0.github.io/Amber/qodana).
