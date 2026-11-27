rootProject.name = "Marky"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":androidApp")
include(":desktopApp")
include(":composeApp")

include(":core:domain")
include(":core:database")
include(":core:data")
include(":core:presentation")
include(":core:designsystem")

include(":feature:notes:domain")
include(":feature:notes:data")
include(":feature:notes:presentation")

include(":feature:note:domain")
include(":feature:note:data")
include(":feature:note:presentation")

include(":feature:search:domain")
include(":feature:search:data")
include(":feature:search:presentation")
