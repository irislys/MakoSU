package com.sukisu.ultra.ui.screen.module

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

/** Stores user-selected module banners independently from module installation paths. */
object ModuleBannerStore {
    private const val DIRECTORY = "module_banners"
    private const val MAX_BYTES = 8 * 1024 * 1024

    suspend fun save(context: Context, moduleId: String, uri: Uri): Boolean = withContext(Dispatchers.IO) {
        val target = bannerFile(context, moduleId)
        val temporary = File(target.parentFile, "${target.name}.tmp")

        runCatching {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(temporary).use { output ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    var bytesCopied = 0
                    while (true) {
                        val count = input.read(buffer)
                        if (count < 0) break
                        bytesCopied += count
                        require(bytesCopied <= MAX_BYTES) { "Banner image is too large" }
                        output.write(buffer, 0, count)
                    }
                }
            } ?: error("Cannot open selected image")

            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(temporary.path, bounds)
            require(bounds.outWidth > 0 && bounds.outHeight > 0) { "Selected file is not an image" }

            if (target.exists()) target.delete()
            require(temporary.renameTo(target)) { "Cannot store banner image" }
        }.isSuccess.also {
            if (temporary.exists()) temporary.delete()
        }
    }

    fun remove(context: Context, moduleId: String): Boolean = bannerFile(context, moduleId).delete()

    fun has(context: Context, moduleId: String): Boolean = bannerFile(context, moduleId).isFile

    fun load(context: Context, moduleId: String): ImageBitmap? {
        val file = bannerFile(context, moduleId)
        if (!file.isFile) return null

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.path, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        var sampleSize = 1
        while (bounds.outWidth / sampleSize > 2048 || bounds.outHeight / sampleSize > 2048) {
            sampleSize *= 2
        }
        return BitmapFactory.decodeFile(file.path, BitmapFactory.Options().apply {
            inSampleSize = sampleSize
        })?.asImageBitmap()
    }

    private fun bannerFile(context: Context, moduleId: String): File {
        val directory = File(context.filesDir, DIRECTORY).apply { mkdirs() }
        val stableName = MessageDigest.getInstance("SHA-256")
            .digest(moduleId.toByteArray(Charsets.UTF_8))
            .joinToString(separator = "") { "%02x".format(it) }
        return File(directory, "$stableName.img")
    }
}

@Composable
fun rememberModuleBanner(moduleId: String, revision: Int): ImageBitmap? {
    val context = LocalContext.current
    var banner by remember(moduleId, revision) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(moduleId, revision) {
        banner = withContext(Dispatchers.IO) { ModuleBannerStore.load(context, moduleId) }
    }
    return banner
}
