package xyz.retrixe.wpustudent.kmp

// https://discuss.kotlinlang.org/t/kotlins-version-of-bigdecimal/21762/2
// https://github.com/gciatto/kt-math
// https://github.com/SciProgCentre/kmath/issues/340
// https://github.com/Crossoid/Kotlin-Native-BigDecimal

actual fun Int.toBigDecimal(): BigDecimal {
    TODO("Not yet implemented")
}

actual fun Double.toBigDecimal(): BigDecimal {
    TODO("Not yet implemented")
}

actual val BigDecimalONE: BigDecimal
    get() = TODO("Not yet implemented")

actual class BigDecimal {
    actual fun add(amount: BigDecimal): BigDecimal {
        TODO("Not yet implemented")
    }

    actual fun divide(divisor: BigDecimal, roundingMode: RoundingMode): BigDecimal {
        TODO("Not yet implemented")
    }

    actual fun movePointLeft(divisor: Int): BigDecimal {
        TODO("Not yet implemented")
    }

    actual fun negate(): BigDecimal {
        TODO("Not yet implemented")
    }

    actual fun setScale(scale: Int, roundingMode: RoundingMode): BigDecimal {
        TODO("Not yet implemented")
    }

    actual fun subtract(other: BigDecimal): BigDecimal {
        TODO("Not yet implemented")
    }

    actual fun toInt(): Int {
        TODO("Not yet implemented")
    }
}

actual enum class RoundingMode {
    UP,
    DOWN,
}
