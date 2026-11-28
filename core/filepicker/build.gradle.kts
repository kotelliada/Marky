plugins {
    alias(libs.plugins.convention.cmp.library)
}

// App-independent: no project dependencies, no app strings, so it can be reused in another app.
kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
    }
}
