package com.sukisu.ultra.ui.screen.home

enum class HomeLayout(val value: String) {
    Standard("standard"),
    MiuixMode("miuix_mode");

    companion object {
        const val DEFAULT_VALUE = "standard"

        fun fromValue(value: String?): HomeLayout = when (value) {
            "compact" -> MiuixMode // Migrate the first release of this layout preference.
            else -> entries.firstOrNull { it.value == value } ?: Standard
        }
    }
}
