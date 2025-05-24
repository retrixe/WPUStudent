package xyz.retrixe.wpustudent.utils

import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.SignStyle
import java.time.temporal.ChronoField
import java.util.Locale

val RFC_1123_DATE: DateTimeFormatter = DateTimeFormatterBuilder()
    .parseCaseInsensitive()
    .parseLenient()
    .optionalStart()
    .appendText(ChronoField.DAY_OF_WEEK)
    .appendLiteral(", ")
    .optionalEnd()
    .appendValue(ChronoField.DAY_OF_MONTH, 1, 2, SignStyle.NOT_NEGATIVE)
    .appendLiteral(' ')
    .appendText(ChronoField.MONTH_OF_YEAR)
    .appendLiteral(' ')
    .appendValue(ChronoField.YEAR, 4) // 2 digit year not handled
    .toFormatter(Locale.US)
