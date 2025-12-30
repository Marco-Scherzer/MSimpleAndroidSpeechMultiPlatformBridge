pluginManagement {
    repositories {
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
        google()
        mavenCentral()
    }
}

rootProject.name = "msimplespeechbackend"
include(":app")
include(":MGridBuilder_AndroidVersion")
project(":MGridBuilder_AndroidVersion").projectDir = file("Z:\\MarcoScherzer-Projects\\MGridBuilder_AndroidVersion\\mgridbuilder_androidversion")

 