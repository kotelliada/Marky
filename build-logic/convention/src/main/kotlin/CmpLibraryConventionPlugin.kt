import io.kotelliada.marky.convention.library
import io.kotelliada.marky.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * kmp.library + Compose Multiplatform.
 *
 * Material 3 is deliberately NOT added here: only :core:designsystem may use it (see AGENTS.md section 3),
 * so it declares the dependency itself.
 */
class CmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                // Compose must be applied first so kmp.library enables Android resources.
                apply("org.jetbrains.compose")
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("io.kotelliada.marky.convention.kmp.library")
            }

            dependencies {
                "commonMainImplementation"(libs.library("jetbrains-compose-runtime"))
                "commonMainImplementation"(libs.library("jetbrains-compose-foundation"))
                "commonMainImplementation"(libs.library("jetbrains-compose-ui"))
                "commonMainImplementation"(libs.library("jetbrains-compose-components-resources"))
                "commonMainImplementation"(libs.library("jetbrains-compose-ui-tooling-preview"))

                // Android previews: KMP modules have no build types, so debugImplementation is unavailable.
                "androidMainImplementation"(libs.library("jetbrains-compose-ui-tooling"))
            }
        }
    }
}
