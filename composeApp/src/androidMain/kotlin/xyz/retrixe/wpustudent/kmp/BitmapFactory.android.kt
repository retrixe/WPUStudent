package xyz.retrixe.wpustudent.kmp

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

actual fun decodeBytesToImageBitmap(data: ByteArray): ImageBitmap =
    BitmapFactory.decodeByteArray(data, 0, data.size).asImageBitmap()

