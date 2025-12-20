package xyz.retrixe.wpustudent.kmp

expect fun Int.toBigDecimal(): BigDecimal

expect fun Double.toBigDecimal(): BigDecimal

operator fun BigDecimal.inc(): BigDecimal = add(BigDecimalONE)

operator fun BigDecimal.minus(other: BigDecimal): BigDecimal = subtract(other)

expect val BigDecimalONE: BigDecimal

expect enum class RoundingMode {
    DOWN,
    HALF_EVEN,
    UP,
}

expect class BigDecimal {
    fun add(amount: BigDecimal): BigDecimal
    fun divide(divisor: BigDecimal, roundingMode: RoundingMode): BigDecimal
    fun movePointLeft(divisor: Int): BigDecimal
    fun negate(): BigDecimal
    fun setScale(scale: Int, roundingMode: RoundingMode): BigDecimal
    fun subtract(other: BigDecimal): BigDecimal
    fun toInt(): Int
    fun toPlainString(): String
}
