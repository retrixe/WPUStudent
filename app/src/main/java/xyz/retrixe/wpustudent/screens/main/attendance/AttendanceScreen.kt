package xyz.retrixe.wpustudent.screens.main.attendance

import android.os.Parcelable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ktor.client.HttpClient
import kotlinx.parcelize.Parcelize
import xyz.retrixe.wpustudent.api.endpoints.getAttendedCourses
import xyz.retrixe.wpustudent.api.endpoints.getTermAttendanceSummary
import xyz.retrixe.wpustudent.api.entities.AttendedTerm
import xyz.retrixe.wpustudent.api.entities.CourseAttendanceSummary
import xyz.retrixe.wpustudent.api.entities.StudentBasicInfo
import xyz.retrixe.wpustudent.ui.components.FixedFractionIndicator
import kotlin.math.ceil

@Parcelize
private sealed interface AttendanceSummary : Parcelable {
    object Loading : AttendanceSummary

    object Error : AttendanceSummary

    data class Loaded(
        val summary: List<CourseAttendanceSummary>,
        val courses: List<AttendedTerm>,
    ) : AttendanceSummary
}

// TODO
//  - https://m3.material.io/styles/color/advanced/overview
//  - https://github.com/material-foundation/material-color-utilities
//  - https://material-foundation.github.io/material-theme-builder/
//  - https://developer.android.com/develop/ui/compose/designsystems/custom
@Composable
private fun getThresholdColor(value: Double, threshold: Double) =
    if (value >= threshold + 5) Color(0xFF00BB90)
    else if (value >= threshold) Color(0xFFFFCC02)
    else MaterialTheme.colorScheme.error

@Composable
fun AttendanceScreen(
    paddingValues: PaddingValues,
    httpClient: HttpClient,
    studentBasicInfo: StudentBasicInfo
) {
    var attendanceSummary by rememberSaveable(studentBasicInfo.studentId) {
        mutableStateOf<AttendanceSummary>(AttendanceSummary.Loading)
    }

    LaunchedEffect(studentBasicInfo.studentId) {
        if (attendanceSummary != AttendanceSummary.Loading) return@LaunchedEffect
        try {
            val summary = getTermAttendanceSummary(httpClient, studentBasicInfo.studentId)
            val courses = getAttendedCourses(httpClient, studentBasicInfo.studentId)
            attendanceSummary = AttendanceSummary.Loaded(summary, courses)
        } catch (_: Exception) {
            attendanceSummary = AttendanceSummary.Error
        }
    }

    Column(Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(16.dp))
        Text("Attendance", fontSize = 36.sp, fontWeight = FontWeight.Bold)

        when (attendanceSummary) {
            is AttendanceSummary.Loading -> {
                Spacer(Modifier.weight(1f))
                CircularProgressIndicator(Modifier.size(96.dp).align(Alignment.CenterHorizontally))
                Spacer(Modifier.weight(1f))
            }

            is AttendanceSummary.Loaded -> {
                val summary = (attendanceSummary as AttendanceSummary.Loaded).summary

                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("Total", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    val totalAttendance =
                        summary.sumOf { it.presentCount * 100 / it.totalSessions } / summary.size
                    val lowestThreshold =
                        summary.minOf { it.thresholdPercentage }
                    Text("%.2f".format(totalAttendance) + "%",
                        color = getThresholdColor(totalAttendance, lowestThreshold),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(16.dp))

                LazyColumn(
                    Modifier.width(512.dp).fillMaxWidth().align(Alignment.CenterHorizontally),
                ) { items(summary.sortedBy { it.moduleName }) { course ->
                    val rawAttendance = course.presentCount / course.totalSessions
                    val attendance = rawAttendance * 100
                    val color = getThresholdColor(attendance, course.thresholdPercentage)

                    OutlinedCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text(course.moduleName, fontSize = 20.sp)
                            Spacer(Modifier.height(16.dp))
                            FixedFractionIndicator(Modifier.height(8.dp), rawAttendance, color)
                            Spacer(Modifier.height(8.dp))
                            Text(buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(color = color, fontWeight = FontWeight.Bold),
                                ) {
                                    append("%.2f".format(attendance) + "%")
                                }
                                val presentCount = course.presentCount.toInt()
                                val totalSessions = course.totalSessions.toInt()
                                append(" ($presentCount / $totalSessions sessions)")
                            })
                            if (attendance < (course.thresholdPercentage + 5)) {
                                // (present + x) / (total + x) = threshold
                                // => (present + x) = threshold (total + x)
                                // => present + x = threshold * total + threshold * x
                                // => x - x * threshold = threshold * total - present
                                // => x (1 - threshold) = threshold * total - present
                                // => x = (threshold * total - present) / (1 - threshold)
                                val present = course.presentCount
                                val total = course.totalSessions
                                val threshold = (course.thresholdPercentage + 5) / 100
                                val classesLeft = ((threshold * total) - present) / (1 - threshold)
                                Spacer(Modifier.height(16.dp))
                                Text("Attend ${ceil(classesLeft).toInt()} classes to reach " +
                                        "${(threshold * 100).toInt()}% threshold.")
                            } else {
                                // TODO: Estimate classes left, and how many one should attend
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                } }
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
