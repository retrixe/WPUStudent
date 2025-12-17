package xyz.retrixe.wpustudent.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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

expect fun calculateSkippableClasses(present: Int, total: Int, threshold: Double): Int

expect fun calculateClassesToThreshold(present: Int, total: Int, threshold: Double): Int
