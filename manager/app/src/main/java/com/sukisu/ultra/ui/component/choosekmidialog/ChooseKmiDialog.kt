package com.sukisu.ultra.ui.component.choosekmidialog

import androidx.compose.runtime.Composable
import com.sukisu.ultra.ui.LocalUiMode
import com.sukisu.ultra.ui.UiMode

@Composable
fun ChooseKmiDialog(
    show: Boolean,
    onDismissRequest: () -> Unit,
    onSelected: (String?) -> Unit
) {
    ChooseKmiDialogMiuix(show, onDismissRequest, onSelected)
}