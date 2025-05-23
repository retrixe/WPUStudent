package xyz.retrixe.wpustudent.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

@Composable
fun FixedFractionIndicator(modifier: Modifier = Modifier, fraction: Double, color: Color) {
    // FIXME: Better background color
    Row(modifier.fillMaxWidth().clip(MaterialTheme.shapes.small)) {
        Spacer(modifier.fillMaxWidth(fraction.toFloat()).background(color))
    }
}
