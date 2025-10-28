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
        gradlePluginPortal()

    }
}

rootProject.name = "Campus"
include(":October2025:CursedCountdown")
include(":October2025:PumpkinSplash")
include(":October2025:CovenBookingDesk")
include(":October2025:HauntedThemeSwitcher")
include(":October2025:HalloweenSkeletonPuzzle")
include(":LazyPizzaApp")
