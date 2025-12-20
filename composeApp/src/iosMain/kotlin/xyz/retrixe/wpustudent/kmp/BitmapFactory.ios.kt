package xyz.retrixe.wpustudent.kmp

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image

// Source - https://stackoverflow.com/a/78459732
// Posted by jagadishlakkurcom jagadishlakk
// Retrieved 2025-12-20, License - CC BY-SA 4.0

actual fun decodeBytesToImageBitmap(data: ByteArray): ImageBitmap =
    Image.makeFromEncoded(data).toComposeImageBitmap()

