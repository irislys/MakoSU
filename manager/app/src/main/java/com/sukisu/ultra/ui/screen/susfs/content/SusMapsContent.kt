package com.sukisu.ultra.ui.screen.susfs.content

import androidx.compose.runtime.Composable
import com.sukisu.ultra.ui.LocalUiMode
import com.sukisu.ultra.ui.UiMode
import com.sukisu.ultra.ui.screen.susfs.content.miuix.SusMapsContentMiuix

@Composable
fun SusMapsContent(
    susMaps: Set<String>,
    isLoading: Boolean,
    onAddSusMap: () -> Unit,
    onRemoveSusMap: (String) -> Unit,
    onEditSusMap: ((String) -> Unit)? = null,
    onReset: (() -> Unit)? = null
) {
    SusMapsContentMiuix(
            susMaps = susMaps,
            isLoading = isLoading,
            onAddSusMap = onAddSusMap,
            onRemoveSusMap = onRemoveSusMap,
            onEditSusMap = onEditSusMap,
            onReset = onReset
        )
}
