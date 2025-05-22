package xyz.retrixe.wpustudent.screens.main.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import xyz.retrixe.wpustudent.api.endpoints.getAttendedCourses
import xyz.retrixe.wpustudent.api.endpoints.getTermAttendanceSummary
import xyz.retrixe.wpustudent.api.entities.AttendedTerm
import xyz.retrixe.wpustudent.api.entities.CourseAttendanceSummary
import xyz.retrixe.wpustudent.api.entities.StudentBasicInfo
import java.io.Serializable

private sealed interface AttendanceSummary : Serializable {
    object Loading : AttendanceSummary {
        @Suppress("unused") private fun readResolve(): Any = Loading
    }

    object Error : AttendanceSummary {
        @Suppress("unused") private fun readResolve(): Any = Error
    }

    data class Loaded(
        val summary: List<CourseAttendanceSummary>,
        val courses: List<AttendedTerm>,
    ) : AttendanceSummary
}

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
            val courses = getAttendedCourses(httpClient)
            attendanceSummary = AttendanceSummary.Loaded(summary, courses)
        } catch (_: Exception) {
            attendanceSummary = AttendanceSummary.Error
        }
    }

    // FIXME Properly display this stuff you know
    Column(
        Modifier.fillMaxSize().padding(paddingValues).padding(8.dp, 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (attendanceSummary) {
            is AttendanceSummary.Loading -> {
                CircularProgressIndicator(Modifier.size(192.dp).padding(48.dp))
            }

            is AttendanceSummary.Loaded -> {
                Text(Json.encodeToString((attendanceSummary as AttendanceSummary.Loaded).courses))
            }

            else -> {
                Box(
                    Modifier
                        .size(192.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Error loading profile picture",
                        modifier = Modifier.size(96.dp).align(Alignment.Center)
                    )
                }
            }
        }
    }
}
