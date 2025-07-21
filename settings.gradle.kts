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
        // Tambahkan JitPack hanya jika kamu pakai versi eksperimen dari GitHub
         maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "TagTodoproject"
include(":app")
