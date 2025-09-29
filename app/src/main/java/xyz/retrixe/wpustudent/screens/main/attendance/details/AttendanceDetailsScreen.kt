package xyz.retrixe.wpustudent.screens.main.attendance.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.ktor.client.HttpClient
import kotlinx.coroutines.launch
import xyz.retrixe.wpustudent.api.erp.entities.StudentBasicInfo
import xyz.retrixe.wpustudent.api.erp.entities.THRESHOLD_PERCENTAGE
import xyz.retrixe.wpustudent.models.main.attendance.details.AttendanceDetailsViewModel
import xyz.retrixe.wpustudent.ui.components.FixedFractionIndicator
import xyz.retrixe.wpustudent.utils.calculateClassesToThreshold
import xyz.retrixe.wpustudent.utils.calculateSkippableClasses
import xyz.retrixe.wpustudent.utils.getThresholdColor

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AttendanceDetailsScreen(
    paddingValues: PaddingValues,
    httpClient: HttpClient,
    studentBasicInfo: StudentBasicInfo,
    attendanceThreshold: Double?,
) {
    // FIXME: Pressing on the Attendance button in the bottom navbar doesn't work
    val attendanceDetailsViewModelFactory = AttendanceDetailsViewModel.Factory(httpClient, studentBasicInfo)
    val attendanceDetailsViewModel: AttendanceDetailsViewModel = viewModel(factory = attendanceDetailsViewModelFactory)
    val data by attendanceDetailsViewModel.data.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val refreshState = rememberPullToRefreshState()
    var refreshing by remember { mutableStateOf(false) }

    val filters = remember { mutableStateSetOf<String>() }

    fun refresh() = coroutineScope.launch {
        if (data is AttendanceDetailsViewModel.Data.Loading) return@launch
        refreshing = true
        attendanceDetailsViewModel.fetchData()
        refreshing = false
    }

    Column(Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(16.dp))
        // FIXME: Add Back button
        Text("Attendance Details", fontSize = 36.sp)

        when (data) {
            is AttendanceDetailsViewModel.Data.Loading -> {
                Spacer(Modifier.weight(1f))
                LoadingIndicator(Modifier.size(96.dp).align(Alignment.CenterHorizontally))
                Spacer(Modifier.weight(1f))
            }

            is AttendanceDetailsViewModel.Data.Loaded -> {
                val details = (data as AttendanceDetailsViewModel.Data.Loaded).details
                val (_, _, _, _, subjectDescription, typeDescription) = details.first()

                Spacer(Modifier.height(16.dp))

                val present = details.count { it.studentStatus == "PRESENT" }
                val total = details.size
                val rawAttendance = present.toDouble() / total
                val attendance = rawAttendance * 100
                val threshold = attendanceThreshold ?: (THRESHOLD_PERCENTAGE + 5)
                val color = getThresholdColor(attendance, threshold)

                Text(subjectDescription, fontSize = 24.sp)
                Text("($typeDescription)",
                    color = MaterialTheme.colorScheme.outline, fontSize = 24.sp)
                Spacer(Modifier.height(24.dp))
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("Total", fontSize = 24.sp)
                    Text(
                        "%.2f".format(attendance) + "%",
                        color = getThresholdColor(attendance, threshold),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(8.dp))
                FixedFractionIndicator(Modifier.height(8.dp), rawAttendance, color)
                Spacer(Modifier.height(8.dp))
                Text("$present / $total sessions", Modifier.align(Alignment.End))
                Spacer(Modifier.height(24.dp))
                // TODO: Estimate classes left, and how many one should attend
                if (attendance >= threshold - 5) {
                    val skippableClassesSub = calculateSkippableClasses(
                        present, total, threshold - 5)
                    Text("You can skip $skippableClassesSub classes and stay at ${(threshold - 5).toInt()}%.")
                } else {
                    val classesToSubThreshold = calculateClassesToThreshold(
                        present, total, threshold - 5)
                    Text("Attend $classesToSubThreshold classes to reach ${(threshold - 5).toInt()}%.")
                }
                if (attendance >= threshold) {
                    val skippableClasses = calculateSkippableClasses(
                        present, total, threshold)
                    Text("You can skip $skippableClasses classes and stay at ${threshold.toInt()}%.")
                } else {
                    val classesToThreshold = calculateClassesToThreshold(
                        present, total, threshold)
                    Text("Attend $classesToThreshold classes to reach ${threshold.toInt()}%.")
                }

                Spacer(Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))

                FlowRow(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                    for (type in arrayOf("Present", "Absent")) {
                        FilterChip(
                            onClick = {
                                if (filters.contains(type))
                                    filters.remove(type)
                                else
                                    filters.add(type)
                            },
                            label = { Text(type) },
                            selected = filters.contains(type),
                            leadingIcon = if (filters.contains(type)) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Done,
                                        contentDescription = "$type icon",
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            } else null,
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
                        PullToRefreshDefaults.LoadingIndicator(
                            modifier = Modifier.align(Alignment.TopCenter),
                            isRefreshing = refreshing,
                            // containerColor = MaterialTheme.colorScheme.primaryContainer,
                            color = MaterialTheme.colorScheme.primary, //.onPrimaryContainer,
                            state = refreshState
                        )
                    },
                ) {
                    LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // FIXME We're ALMOST there!!!
                        item {
                            Text("goon")
                        }
                        /* items(
                            summary.sortedBy { it.subjectName + it.subjectType },
                            key = { it.subjectName + it.subjectType }
                        ) { course ->
                            AttendanceCard(course, attendanceThreshold ?: (THRESHOLD_PERCENTAGE + 5))
                        } */
                    }
                }
            }

            else -> {
                Spacer(Modifier.weight(1f))
                Text("Failed to load attendance details!\n" +
                        "Try reopening the app, or log out and log back in.\n" +
                        "If not working, open an issue at: https://github.com/retrixe/WPUStudent/issues",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.weight(1f))
            }
        }
    }
}
