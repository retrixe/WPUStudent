package xyz.retrixe.wpustudent.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

val RFC_1123_DATE = LocalDate.Format {
    //.parseCaseInsensitive()
    //.parseLenient()
    //.optionalStart()
    dayOfWeek(DayOfWeekNames.ENGLISH_FULL)
    chars(", ")
    // .optionalEnd()
    day(Padding.NONE)
    char(' ')
    monthName(MonthNames.ENGLISH_FULL)
    char(' ')
    year()
}

val DD_MM_YYYY_DATE = LocalDate.Format {
    day()
    char('/')
    monthNumber()
    char('/')
    year()
}
