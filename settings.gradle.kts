pluginManagement {
    includeBuild("build-logic")
    repositories {
        maven { url = uri("") }
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("") }
        google()
        mavenCentral()
    }
}

rootProject.name = "ComposeTrackNew"
include(":demo")
