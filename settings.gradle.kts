pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven ( url = "https://www.jitpack.io") // self - Client
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven ( url = "https://www.jitpack.io") // self - Client
    }
}

rootProject.name = "DemoKart"
include(":app")
include(":demokartadmin")
