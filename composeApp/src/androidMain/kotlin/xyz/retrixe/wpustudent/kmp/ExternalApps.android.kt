package xyz.retrixe.wpustudent.kmp

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import androidx.core.net.toUri
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import java.util.Calendar

actual fun openCalendar(androidContext: Any?, date: LocalDate) {
    if (androidContext !is Context) {
        throw IllegalArgumentException("androidContext is not a valid Context")
    }
    val builder = CalendarContract.CONTENT_URI.buildUpon()
    builder.appendPath("time")
    val calendar = Calendar.getInstance()
    calendar.set(date.year, date.month.number - 1, date.day)
    ContentUris.appendId(builder, calendar.timeInMillis)
    val intent = Intent(Intent.ACTION_VIEW).setData(builder.build())
    androidContext.startActivity(intent)
}

actual fun openUrl(androidContext: Any?, url: String) {
    if (androidContext !is Context) {
        throw IllegalArgumentException("androidContext is not a valid Context")
    }
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    androidContext.startActivity(intent)
}
