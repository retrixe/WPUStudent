package xyz.retrixe.wpustudent.screens.main.holidays

import android.os.Parcelable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ktor.client.HttpClient
import kotlinx.parcelize.Parcelize
import xyz.retrixe.wpustudent.api.endpoints.getHolidays
import xyz.retrixe.wpustudent.api.entities.Holiday
import xyz.retrixe.wpustudent.api.entities.StudentBasicInfo
import java.time.LocalDate
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

@Parcelize
private sealed interface Holidays : Parcelable {
    object Loading : Holidays

    object Error : Holidays

    data class Loaded(val holidays: List<Holiday>) : Holidays
}

@Composable
fun HolidayCard(holiday: Holiday) {
    val startDate = LocalDate.parse(holiday.startDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    val endDate = LocalDate.parse(holiday.endDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    OutlinedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(holiday.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                Text(holiday.subType)
            }
            Spacer(Modifier.height(16.dp))
            Text(buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(RFC_1123_DATE.format(startDate))
                }
                if (startDate != endDate) {
                    append(" through ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(RFC_1123_DATE.format(endDate))
                    }
                }
            }, fontSize = 20.sp)
        }
    }
}

@Composable
fun HolidaysScreen(
    paddingValues: PaddingValues,
    httpClient: HttpClient,
    studentBasicInfo: StudentBasicInfo
) {
    var holidays by rememberSaveable(studentBasicInfo.studentId) {
        mutableStateOf<Holidays>(Holidays.Loading)
    }

    LaunchedEffect(studentBasicInfo.studentId) {
        if (holidays != Holidays.Loading) return@LaunchedEffect
        try {
            val data = getHolidays(httpClient, studentBasicInfo.studentId)
            holidays = Holidays.Loaded(data)
        } catch (_: Exception) {
            holidays = Holidays.Error
        }
    }

    Column(Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(16.dp))
        Text("Holidays", fontSize = 36.sp, fontWeight = FontWeight.Bold)

        when (holidays) {
            is Holidays.Loading -> {
                Spacer(Modifier.weight(1f))
                CircularProgressIndicator(Modifier.size(96.dp).align(Alignment.CenterHorizontally))
                Spacer(Modifier.weight(1f))
            }

            is Holidays.Loaded -> {
                val holidays = (holidays as Holidays.Loaded).holidays

                Spacer(Modifier.height(16.dp))

                LazyColumn(
                    Modifier.width(512.dp).fillMaxWidth().align(Alignment.CenterHorizontally),
                ) {
                    val sortedHolidays = holidays.sortedBy { it.startDate }
                    val pastHolidays = sortedHolidays.takeWhile {
                        LocalDate
                            .parse(it.startDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            .isBefore(LocalDate.now())
                    }
                    val upcomingHolidays = sortedHolidays
                        .takeLast(sortedHolidays.size - pastHolidays.size)

                    item { if (upcomingHolidays.isNotEmpty()) {
                        Text("Upcoming holidays", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(16.dp))
                    } }
                    items(upcomingHolidays) { holiday ->
                        HolidayCard(holiday)
                        Spacer(Modifier.height(16.dp))
                    }

                    item { if (pastHolidays.isNotEmpty()) {
                        if (upcomingHolidays.isNotEmpty()) {
                            Spacer(Modifier.height(16.dp))
                        }
                        Text("Past holidays", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(16.dp))
                    } }
                    items(pastHolidays) { holiday ->
                        HolidayCard(holiday)
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }

            else -> {
                Spacer(Modifier.weight(1f))
                Text("Failed to load holidays!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.weight(1f))
            }
        }
    }
}
