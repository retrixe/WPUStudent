package xyz.retrixe.wpustudent.screens.main.holidays

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.ktor.client.HttpClient
import kotlinx.coroutines.launch
import xyz.retrixe.wpustudent.api.entities.Holiday
import xyz.retrixe.wpustudent.api.entities.StudentBasicInfo
import xyz.retrixe.wpustudent.models.main.holidays.HolidaysViewModel
import xyz.retrixe.wpustudent.utils.RFC_1123_DATE
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
private fun HolidayCard(holiday: Holiday) {
    val startDate = LocalDate.parse(holiday.startDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    val endDate = LocalDate.parse(holiday.endDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    OutlinedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(holiday.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                Text(holiday.subType, Modifier.padding(2.dp))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HolidaysScreen(
    paddingValues: PaddingValues,
    httpClient: HttpClient,
    studentBasicInfo: StudentBasicInfo
) {
    val holidaysViewModelFactory = HolidaysViewModel.Factory(httpClient, studentBasicInfo)
    val holidaysViewModel: HolidaysViewModel = viewModel(factory = holidaysViewModelFactory)
    val data by holidaysViewModel.data.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val refreshState = rememberPullToRefreshState()
    var refreshing by remember { mutableStateOf(false) }

    fun refresh() = coroutineScope.launch {
        if (data is HolidaysViewModel.Data.Loading) return@launch
        refreshing = true
        holidaysViewModel.fetchData()
        refreshing = false
    }

    Column(Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(16.dp))
        Text("Holidays", fontSize = 36.sp, fontWeight = FontWeight.Bold)

        when (data) {
            is HolidaysViewModel.Data.Loading -> {
                Spacer(Modifier.weight(1f))
                CircularProgressIndicator(Modifier.size(96.dp).align(Alignment.CenterHorizontally))
                Spacer(Modifier.weight(1f))
            }

            is HolidaysViewModel.Data.Loaded -> {
                val holidays = (data as HolidaysViewModel.Data.Loaded).holidays

                Spacer(Modifier.height(16.dp))

                PullToRefreshBox(
                    isRefreshing = refreshing,
                    onRefresh = ::refresh,
                    modifier = Modifier.width(512.dp).fillMaxWidth().align(Alignment.CenterHorizontally),
                    state = refreshState,
                    indicator = {
                        PullToRefreshDefaults.Indicator(
                            modifier = Modifier.align(Alignment.TopCenter),
                            isRefreshing = refreshing,
                            // containerColor = MaterialTheme.colorScheme.primaryContainer,
                            color = MaterialTheme.colorScheme.primary, //.onPrimaryContainer,
                            state = refreshState
                        )
                    },
                ) {
                    LazyColumn(Modifier.fillMaxSize()) {
                        val sortedHolidays = holidays.sortedBy { it.startDate }
                        val pastHolidays = sortedHolidays.takeWhile {
                            LocalDate
                                .parse(it.startDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                                .isBefore(LocalDate.now())
                        }
                        val upcomingHolidays = sortedHolidays
                            .takeLast(sortedHolidays.size - pastHolidays.size)

                        if (upcomingHolidays.isNotEmpty()) {
                            item {
                                Text(
                                    "Upcoming holidays",
                                    fontSize = 24.sp, fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(16.dp))
                            }
                        }
                        items(upcomingHolidays) { holiday ->
                            HolidayCard(holiday)
                            Spacer(Modifier.height(16.dp))
                        }

                        if (pastHolidays.isNotEmpty()) {
                            item {
                                if (upcomingHolidays.isNotEmpty()) {
                                    Spacer(Modifier.height(16.dp))
                                }
                                Text(
                                    "Past holidays",
                                    fontSize = 24.sp, fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(16.dp))
                            }
                        }
                        items(pastHolidays) { holiday ->
                            HolidayCard(holiday)
                            Spacer(Modifier.height(16.dp))
                        }
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
