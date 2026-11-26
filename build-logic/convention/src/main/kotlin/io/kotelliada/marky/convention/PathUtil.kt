package io.kotelliada.marky.convention

import org.gradle.api.Project

private const val BASE_PACKAGE = "io.kotelliada.marky"

/**
 * Derives a package / Android namespace from the module path,
 * e.g. `:feature:notes:presentation` -> `io.kotelliada.marky.feature.notes.presentation`.
 */
fun Project.pathToPackageName(): String {
    val relativePackageName = path
        .replace(':', '.')
        .replace('-', '_')
        .lowercase()
    return "$BASE_PACKAGE$relativePackageName"
}
