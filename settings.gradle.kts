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
        google()
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

rootProject.name = "Campus"
include(":October2025:CursedCountdown")
include(":October2025:PumpkinSplash")
include(":October2025:CovenBookingDesk")
include(":October2025:HauntedThemeSwitcher")
include(":October2025:HalloweenSkeletonPuzzle")
