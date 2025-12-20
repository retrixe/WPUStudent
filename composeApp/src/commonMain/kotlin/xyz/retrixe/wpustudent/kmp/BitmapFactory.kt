package xyz.retrixe.wpustudent.kmp

import androidx.compose.ui.graphics.ImageBitmap

expect fun decodeBytesToImageBitmap(data: ByteArray): ImageBitmap
