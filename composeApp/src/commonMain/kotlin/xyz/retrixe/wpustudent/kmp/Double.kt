package xyz.retrixe.wpustudent.kmp

// https://youtrack.jetbrains.com/issue/KT-9374/Suggestion-add-a-toStringprecision-Int-to-Float-and-Double-types
// https://youtrack.jetbrains.com/issue/KT-21644/Add-utility-function-for-formatting-of-decimal-numbers-to-Kotlin-common-library
// https://youtrack.jetbrains.com/issue/KT-25506/Stdlib-String.format-in-common

fun Double.toFixedString(n: Int): String =
    /*if (this == 0.0) {
        return "0.00"
    }
    // This is the dumbest code I have written.
    // I could use BigDecimal, but... hey...
    var rounded = (this * 10.0.pow(n)).roundToInt()
    var decimalPart = ""
    for (i in 1..n) {
        decimalPart += rounded % 10
        rounded /= 10
    }
    var integerPart = ""
    if (rounded == 0) {
        integerPart = "0"
    } else {
        while (rounded > 0) {
            integerPart += rounded % 10
            rounded /= 10
        }
    }
    return integerPart.reversed() + "." + decimalPart.reversed()*/
    // Another viable option using String.format, "%.2f" and native code:
    // https://stackoverflow.com/questions/64495182/kotlin-native-ios-string-formatting-with-vararg
    // but, fine.
    this.toBigDecimal().setScale(n, RoundingMode.HALF_EVEN).toPlainString()
