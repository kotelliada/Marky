plugins {
    alias(libs.plugins.convention.cmp.application)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.domain)
            implementation(projects.core.data)
            implementation(projects.core.presentation)
            implementation(projects.core.designsystem)

            implementation(projects.feature.notes.domain)
            implementation(projects.feature.notes.data)
            implementation(projects.feature.notes.presentation)

            implementation(projects.feature.note.domain)
            implementation(projects.feature.note.data)
            implementation(projects.feature.note.presentation)

            implementation(projects.feature.search.domain)
            implementation(projects.feature.search.data)
            implementation(projects.feature.search.presentation)
        }
    }
}
