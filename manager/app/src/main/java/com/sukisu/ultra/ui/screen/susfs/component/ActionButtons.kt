package com.sukisu.ultra.ui.screen.susfs.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sukisu.ultra.ui.LocalUiMode
import com.sukisu.ultra.ui.UiMode
import com.sukisu.ultra.ui.screen.susfs.component.miuix.BottomActionButtonsMiuix

@Composable
fun BottomActionButtons(
    modifier: Modifier = Modifier,
    primaryButtonText: String,
    onPrimaryClick: () -> Unit,
    secondaryButtonText: String? = null,
    onSecondaryClick: (() -> Unit)? = null,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    BottomActionButtonsMiuix(
            modifier = modifier,
            primaryButtonText = primaryButtonText,
            onPrimaryClick = onPrimaryClick,
            secondaryButtonText = secondaryButtonText,
            onSecondaryClick = onSecondaryClick,
            isLoading = isLoading,
            enabled = enabled
        )
}
