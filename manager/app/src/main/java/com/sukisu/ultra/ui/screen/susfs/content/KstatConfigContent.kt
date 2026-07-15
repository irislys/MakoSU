package com.sukisu.ultra.ui.screen.susfs.content

import androidx.compose.runtime.Composable
import com.sukisu.ultra.ui.LocalUiMode
import com.sukisu.ultra.ui.UiMode
import com.sukisu.ultra.ui.screen.susfs.content.miuix.KstatConfigContentMiuix

@Composable
fun KstatConfigContent(
    kstatConfigs: Set<String>,
    addKstatPaths: Set<String>,
    isLoading: Boolean,
    onAddKstatStatically: () -> Unit,
    onAddKstat: () -> Unit,
    onRemoveKstatConfig: (String) -> Unit,
    onEditKstatConfig: ((String) -> Unit)? = null,
    onRemoveAddKstat: (String) -> Unit,
    onEditAddKstat: ((String) -> Unit)? = null,
    onUpdateKstat: (String) -> Unit,
    onUpdateKstatFullClone: (String) -> Unit
) {
    KstatConfigContentMiuix(
            kstatConfigs = kstatConfigs,
            addKstatPaths = addKstatPaths,
            isLoading = isLoading,
            onAddKstatStatically = onAddKstatStatically,
            onAddKstat = onAddKstat,
            onRemoveKstatConfig = onRemoveKstatConfig,
            onEditKstatConfig = onEditKstatConfig,
            onRemoveAddKstat = onRemoveAddKstat,
            onEditAddKstat = onEditAddKstat,
            onUpdateKstat = onUpdateKstat,
            onUpdateKstatFullClone = onUpdateKstatFullClone
        )
}
