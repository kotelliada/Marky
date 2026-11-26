import io.kotelliada.marky.convention.configureKotlinMultiplatform
import io.kotelliada.marky.convention.library
import io.kotelliada.marky.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

/**
 * :composeApp — a KMP *library* (Gradle 9 forbids com.android.application in KMP modules)
 * that wires every module together and produces the iOS `ComposeApp` framework.
 */
class CmpApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.kotlin.multiplatform.library")
                apply("org.jetbrains.kotlin.multiplatform")
                apply("org.jetbrains.compose")
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            configureKotlinMultiplatform(enableAndroidResources = true)

            extensions.configure<KotlinMultiplatformExtension> {
                targets.withType(KotlinNativeTarget::class.java).configureEach {
                    binaries.framework {
                        baseName = "ComposeApp"
                        isStatic = true
                        binaryOption("bundleId", "io.kotelliada.marky.ComposeApp")
                    }
                }
            }

            dependencies {
                "commonMainImplementation"(libs.library("jetbrains-compose-runtime"))
                "commonMainImplementation"(libs.library("jetbrains-compose-foundation"))
                "commonMainImplementation"(libs.library("jetbrains-compose-ui"))
                "commonMainImplementation"(libs.library("jetbrains-compose-components-resources"))
                "commonMainImplementation"(libs.library("jetbrains-compose-ui-tooling-preview"))
                "commonMainImplementation"(libs.library("kotlinx-coroutines-core"))
                "commonMainImplementation"(libs.library("kotlinx-serialization-json"))

                "androidMainImplementation"(libs.library("jetbrains-compose-ui-tooling"))

                "commonTestImplementation"(libs.library("kotlin-test"))
                "commonTestImplementation"(libs.library("assertk"))
            }
        }
    }
}
