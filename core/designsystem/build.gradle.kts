plugins {
    alias(libs.plugins.convention.cmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // The only module allowed to use Material 3: Margin components wrap it.
            implementation(libs.jetbrains.compose.material3)
        }
    }
}
