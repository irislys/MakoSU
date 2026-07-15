package com.sukisu.ultra.ui

import androidx.compose.runtime.staticCompositionLocalOf

enum class UiMode(val value: String) {
    Miuix("miuix");

    companion object {
        // MakoSU now ships a single Miuix interface. Ignore legacy saved values.
        fun fromValue(value: String): UiMode = Miuix

        val DEFAULT_VALUE = Miuix.value
    }
}

val LocalUiMode = staticCompositionLocalOf { UiMode.Miuix }
