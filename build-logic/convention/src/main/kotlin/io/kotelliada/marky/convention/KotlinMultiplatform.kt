package io.kotelliada.marky.convention

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Shared KMP setup for every library module and :composeApp:
 * Android (KMP library target with host tests), Desktop (`jvm("desktop")`) and iOS (arm64 + simulator arm64).
 */
internal fun Project.configureKotlinMultiplatform(
    enableAndroidResources: Boolean = false,
) {
    extensions.configure<KotlinMultiplatformExtension> {
        extensions.configure<KotlinMultiplatformAndroidLibraryTarget> {
            namespace = pathToPackageName()
            compileSdk = libs.intVersion("projectCompileSdkVersion")
            minSdk = libs.intVersion("projectMinSdkVersion")

            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
            }

            if (enableAndroidResources) {
                androidResources {
                    enable = true
                }
            }

            // commonTest + androidHostTest -> testAndroidHostTest (local JVM unit tests only).
            withHostTest {
                isIncludeAndroidResources = enableAndroidResources
            }
        }

        jvm("desktop") {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
            }
        }

        iosArm64()
        iosSimulatorArm64()

        applyDefaultHierarchyTemplate()

        compilerOptions {
            freeCompilerArgs.add("-Xexpect-actual-classes")
            optIn.add("kotlin.RequiresOptIn")
            optIn.add("kotlin.time.ExperimentalTime")
        }
    }
}
