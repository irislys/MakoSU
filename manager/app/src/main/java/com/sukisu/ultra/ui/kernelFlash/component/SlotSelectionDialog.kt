package com.sukisu.ultra.ui.kernelFlash.component

import androidx.compose.runtime.Composable
import com.sukisu.ultra.ui.LocalUiMode
import com.sukisu.ultra.ui.UiMode

@Composable
fun SlotSelectionDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onSlotSelected: (String) -> Unit,
) {
    SlotSelectionDialogMiuix(
            show = show,
            onDismiss = onDismiss,
            onSlotSelected = onSlotSelected
        )
}
