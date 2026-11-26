import com.android.build.api.dsl.ApplicationExtension
import io.kotelliada.marky.convention.intVersion
import io.kotelliada.marky.convention.library
import io.kotelliada.marky.convention.libs
import io.kotelliada.marky.convention.stringVersion
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

/**
 * :androidApp — thin Android application shell around :composeApp.
 */
class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            extensions.configure<ApplicationExtension> {
                namespace = libs.stringVersion("projectApplicationId")
                compileSdk = libs.intVersion("projectCompileSdkVersion")

                defaultConfig {
                    applicationId = libs.stringVersion("projectApplicationId")
                    minSdk = libs.intVersion("projectMinSdkVersion")
                    targetSdk = libs.intVersion("projectTargetSdkVersion")
                    versionCode = libs.intVersion("projectVersionCode")
                    versionName = libs.stringVersion("projectVersionName")
                }
                packaging {
                    resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                    }
                }
                buildTypes {
                    getByName("release") {
                        isMinifyEnabled = false
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro",
                        )
                    }
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }
                buildFeatures {
                    compose = true
                }
            }

            extensions.configure<KotlinAndroidProjectExtension> {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }

            dependencies {
                "implementation"(libs.library("jetbrains-compose-ui-tooling-preview"))
                "debugImplementation"(libs.library("jetbrains-compose-ui-tooling"))
            }
        }
    }
}
