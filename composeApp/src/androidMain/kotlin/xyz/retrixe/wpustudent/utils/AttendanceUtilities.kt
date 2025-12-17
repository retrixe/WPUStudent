package xyz.retrixe.wpustudent.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import java.math.RoundingMode

val SUCCESS_COLOR = Color(0xFF00BB90)
val WARNING_COLOR = Color(0xFFFFCC02)

// TODO
//  - https://m3.material.io/styles/color/advanced/overview
//  - https://github.com/material-foundation/material-color-utilities
//  - https://material-foundation.github.io/material-theme-builder/
//  - https://developer.android.com/develop/ui/compose/designsystems/custom
@Composable
fun getThresholdColor(value: Double, threshold: Double) =
    if (value >= threshold) SUCCESS_COLOR
    else if (value >= threshold - 5) WARNING_COLOR
    else MaterialTheme.colorScheme.error

fun calculateSkippableClasses(present: Int, total: Int, threshold: Double): Int =
// present / (total + x) = threshold
// => present = threshold (total + x)
// => present = threshold * total + threshold * x
// => threshold * x = present - threshold * total
// => x = present - (threshold * total) / threshold
    // => x = (present / threshold) - total
    if (threshold != 100.0)
        present.toBigDecimal().divide(threshold.toBigDecimal().movePointLeft(2), RoundingMode.DOWN)
            .setScale(0, RoundingMode.DOWN) // Just for added safety...
            .toInt() - total
    else 0

fun calculateClassesToThreshold(present: Int, total: Int, threshold: Double): Int =
// (present + x) / (total + x) = threshold
// => (present + x) = threshold (total + x)
// => present + x = threshold * total + threshold * x
// => x - x * threshold = threshold * total - present
// => x (1 - threshold) = threshold * total - present
    // => x = (threshold * total - present) / (1 - threshold)
    if (threshold != 100.0)
        ((threshold * total).toBigDecimal().movePointLeft(2) - present.toBigDecimal())
            .divide(threshold.toBigDecimal().movePointLeft(2).negate().inc(), RoundingMode.UP)
            .setScale(0, RoundingMode.UP) // Just for added safety...
            .toInt()
    else if (total == present) 0
    else Int.MAX_VALUE
