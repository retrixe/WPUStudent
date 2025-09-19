package xyz.retrixe.wpustudent.screens.main.attendance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.ktor.client.HttpClient
import kotlinx.coroutines.launch
import xyz.retrixe.wpustudent.api.erp.entities.StudentBasicInfo
import xyz.retrixe.wpustudent.api.pwc.entities.CourseAttendanceSummary
import xyz.retrixe.wpustudent.models.main.attendance.AttendanceViewModel
import xyz.retrixe.wpustudent.ui.components.FixedFractionIndicator
import kotlin.math.ceil

// TODO
//  - https://m3.material.io/styles/color/advanced/overview
//  - https://github.com/material-foundation/material-color-utilities
//  - https://material-foundation.github.io/material-theme-builder/
//  - https://developer.android.com/develop/ui/compose/designsystems/custom
@Composable
private fun getThresholdColor(value: Double, threshold: Double) =
    if (value >= threshold) Color(0xFF00BB90)
    else if (value >= threshold - 5) Color(0xFFFFCC02)
    else MaterialTheme.colorScheme.error

@Composable
private fun AttendanceCard(course: CourseAttendanceSummary, threshold: Double) {
    val rawAttendance = course.presentCount / course.totalSessions
    val attendance = rawAttendance * 100
    val color = getThresholdColor(attendance, threshold)

    OutlinedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(course.moduleName, fontSize = 20.sp)
            Spacer(Modifier.height(16.dp))
            FixedFractionIndicator(Modifier.height(8.dp), rawAttendance, color)
            Spacer(Modifier.height(8.dp))
            Text(buildAnnotatedString {
                withStyle(style = SpanStyle(color = color, fontWeight = FontWeight.Bold)) {
                    append("%.2f".format(attendance) + "%")
                }
                val presentCount = course.presentCount.toInt()
                val totalSessions = course.totalSessions.toInt()
                append(" ($presentCount / $totalSessions sessions)")
            })
            Spacer(Modifier.height(16.dp))
            if (attendance < threshold) {
                // (present + x) / (total + x) = threshold
                // => (present + x) = threshold (total + x)
                // => present + x = threshold * total + threshold * x
                // => x - x * threshold = threshold * total - present
                // => x (1 - threshold) = threshold * total - present
                // => x = (threshold * total - present) / (1 - threshold)
                val present = course.presentCount
                val total = course.totalSessions
                val threshold = threshold / 100
                val classesLeft = ((threshold * total) - present) / (1 - threshold)
                Text(
                    "Attend ${ceil(classesLeft).toInt()} classes to reach " +
                            "${(threshold * 100).toInt()}% threshold."
                )
            } else {
                // present / (total + x) = threshold
                // => present = threshold (total + x)
                // => present = threshold * total + threshold * x
                // => threshold * x = present - threshold * total
                // => x = present - (threshold * total) / threshold
                // => x = (present / threshold) - total
                val present = course.presentCount
                val total = course.totalSessions
                val threshold = threshold / 100
                val skippableClasses = (present / threshold) - total
                Text(
                    "You can skip ${ceil(skippableClasses).toInt()} classes and stay at the " +
                            "${(threshold * 100).toInt()}% threshold."
                )
                // TODO: Estimate classes left, and how many one should attend
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    paddingValues: PaddingValues,
    httpClient: HttpClient,
    studentBasicInfo: StudentBasicInfo,
    attendanceThresholdOverride: Double?,
) {
    val attendanceViewModelFactory = AttendanceViewModel.Factory(httpClient, studentBasicInfo)
    val attendanceViewModel: AttendanceViewModel = viewModel(factory = attendanceViewModelFactory)
    val data by attendanceViewModel.data.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val refreshState = rememberPullToRefreshState()
    var refreshing by remember { mutableStateOf(false) }

    fun refresh() = coroutineScope.launch {
        if (data is AttendanceViewModel.Data.Loading) return@launch
        refreshing = true
        attendanceViewModel.fetchData()
        refreshing = false
    }

    Column(Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(16.dp))
        Text("Attendance", fontSize = 36.sp, fontWeight = FontWeight.Bold)

        when (data) {
            is AttendanceViewModel.Data.Loading -> {
                Spacer(Modifier.weight(1f))
                CircularProgressIndicator(Modifier.size(96.dp).align(Alignment.CenterHorizontally))
                Spacer(Modifier.weight(1f))
            }

            is AttendanceViewModel.Data.Loaded -> {
                val summary = (data as AttendanceViewModel.Data.Loaded).summary

                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("Total", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    if (summary.isNotEmpty()) {
                        val totalAttendance =
                            summary.sumOf { it.presentCount * 100 / it.totalSessions } / summary.size
                        val lowestThreshold =
                            attendanceThresholdOverride ?: summary.minOf { it.thresholdPercentage + 5 }
                        Text(
                            "%.2f".format(totalAttendance) + "%",
                            color = getThresholdColor(totalAttendance, lowestThreshold),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            "N/A",
                            color = MaterialTheme.colorScheme.outline,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

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
                        items(summary.sortedBy { it.moduleName }) { course ->
                            AttendanceCard(course,
                                attendanceThresholdOverride ?: (course.thresholdPercentage + 5))
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
            }

            else -> {
                Spacer(Modifier.weight(1f))
                Text("Failed to load attendance data!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.weight(1f))
            }
        }
    }
}
