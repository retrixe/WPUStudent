package xyz.retrixe.wpustudent.kmp

actual fun Int.toBigDecimal(): BigDecimal = java.math.BigDecimal.valueOf(this.toLong())

actual fun Double.toBigDecimal(): BigDecimal = java.math.BigDecimal.valueOf(this)

actual val BigDecimalONE: java.math.BigDecimal = java.math.BigDecimal.ONE

actual typealias BigDecimal = java.math.BigDecimal

actual typealias RoundingMode = java.math.RoundingMode
