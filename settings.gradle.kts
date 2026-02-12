pluginManagement {
    repositories {
        maven("https://dl.google.com/dl/android/maven2")
        maven("https://maven.google.com")
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        maven("https://dl.google.com/dl/android/maven2")
        maven("https://maven.google.com")
        google()
        mavenCentral()
    }
}

rootProject.name = "EndProj"
include(":app")
