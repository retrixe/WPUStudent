package xyz.retrixe.wpustudent.utils

import java.math.RoundingMode

actual fun calculateSkippableClasses(present: Int, total: Int, threshold: Double): Int =
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

actual fun calculateClassesToThreshold(present: Int, total: Int, threshold: Double): Int =
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
