package com.sukisu.ultra.ui.screen.susfs.content

import androidx.compose.runtime.Composable
import com.sukisu.ultra.ui.LocalUiMode
import com.sukisu.ultra.ui.UiMode
import com.sukisu.ultra.ui.screen.susfs.content.miuix.SusLoopPathsContentMiuix

@Composable
fun SusLoopPathsContent(
    susLoopPaths: Set<String>,
    isLoading: Boolean,
    onAddLoopPath: () -> Unit,
    onRemoveLoopPath: (String) -> Unit,
    onEditLoopPath: ((String) -> Unit)? = null,
    onReset: (() -> Unit)? = null
) {
    SusLoopPathsContentMiuix(
            susLoopPaths = susLoopPaths,
            isLoading = isLoading,
            onAddLoopPath = onAddLoopPath,
            onRemoveLoopPath = onRemoveLoopPath,
            onEditLoopPath = onEditLoopPath,
            onReset = onReset
        )
}
