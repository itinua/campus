pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
//    plugins{
//        id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
//    }

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
include(":LazyPizzaUtil")

