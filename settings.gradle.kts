pluginManagement {
    repositories {
        google()  // Add this line to use the Google repository globally
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()  // This ensures Google repository is used globally
        mavenCentral()
    }
}

rootProject.name = "Medicine Application"
include(":app")
