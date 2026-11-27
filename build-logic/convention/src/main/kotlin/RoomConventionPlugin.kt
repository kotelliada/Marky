import androidx.room.gradle.RoomExtension
import io.kotelliada.marky.convention.library
import io.kotelliada.marky.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Room (KMP) with the bundled SQLite driver. Apply on top of `kmp.library`; used by :core:database only.
 */
class RoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.devtools.ksp")
                apply("androidx.room")
            }

            extensions.configure<RoomExtension> {
                schemaDirectory("$projectDir/schemas")
            }

            dependencies {
                "commonMainApi"(libs.library("androidx-room-runtime"))
                "commonMainApi"(libs.library("sqlite-bundled"))

                val roomCompiler = libs.library("androidx-room-compiler")
                "kspAndroid"(roomCompiler)
                "kspDesktop"(roomCompiler)
                "kspIosArm64"(roomCompiler)
                "kspIosSimulatorArm64"(roomCompiler)
            }
        }
    }
}
