pluginManagement {
    repositories {
        google()
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

rootProject.name = "LiveApp"
include(":app")
include(":core")
include(":data")
include(":domain")
include(":features:auth")
include(":features:streaming")
include(":features:chat")
include(":features:settings")