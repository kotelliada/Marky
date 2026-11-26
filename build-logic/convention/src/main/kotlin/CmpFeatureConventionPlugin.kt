import io.kotelliada.marky.convention.library
import io.kotelliada.marky.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * cmp.library + everything a feature presentation module needs:
 * core:presentation, core:designsystem, Koin, Navigation, Lifecycle and SavedState.
 */
class CmpFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("io.kotelliada.marky.convention.cmp.library")
            }

            dependencies {
                "commonMainImplementation"(project(":core:domain"))
                "commonMainImplementation"(project(":core:presentation"))
                "commonMainImplementation"(project(":core:designsystem"))

                "commonMainImplementation"(platform(libs.library("koin-bom")))
                "commonMainImplementation"(libs.library("koin-core"))
                "commonMainImplementation"(libs.library("koin-compose"))
                "commonMainImplementation"(libs.library("koin-compose-viewmodel"))
                "androidMainImplementation"(platform(libs.library("koin-bom")))
                "androidMainImplementation"(libs.library("koin-android"))

                "commonMainImplementation"(libs.library("jetbrains-lifecycle-viewmodel"))
                "commonMainImplementation"(libs.library("jetbrains-lifecycle-viewmodel-compose"))
                "commonMainImplementation"(libs.library("jetbrains-lifecycle-runtime-compose"))
                "commonMainImplementation"(libs.library("jetbrains-lifecycle-viewmodel-savedstate"))
                "commonMainImplementation"(libs.library("jetbrains-savedstate"))
                "commonMainImplementation"(libs.library("jetbrains-navigation-compose"))

                "commonTestImplementation"(libs.library("kotlinx-coroutines-test"))
                "commonTestImplementation"(libs.library("turbine"))
            }
        }
    }
}
