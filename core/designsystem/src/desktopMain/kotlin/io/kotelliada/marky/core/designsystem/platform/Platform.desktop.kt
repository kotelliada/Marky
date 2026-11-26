package io.kotelliada.marky.core.designsystem.platform

internal actual val usesCommandKey: Boolean =
    System.getProperty("os.name").orEmpty().startsWith("Mac", ignoreCase = true)

internal actual val hasHoverTooltips: Boolean = true
