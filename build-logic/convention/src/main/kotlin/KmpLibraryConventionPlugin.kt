import io.kotelliada.marky.convention.configureKotlinMultiplatform
import io.kotelliada.marky.convention.library
import io.kotelliada.marky.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Base plugin for every KMP module without Compose (domain and data layers).
 */
class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.kotlin.multiplatform.library")
                apply("org.jetbrains.kotlin.multiplatform")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            configureKotlinMultiplatform(
                enableAndroidResources = pluginManager.hasPlugin("org.jetbrains.compose"),
            )

            dependencies {
                "commonMainImplementation"(libs.library("kotlinx-coroutines-core"))
                "commonMainImplementation"(libs.library("kotlinx-serialization-json"))

                "commonTestImplementation"(libs.library("kotlin-test"))
                "commonTestImplementation"(libs.library("assertk"))
            }
        }
    }
}
