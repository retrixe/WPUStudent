package xyz.retrixe.wpustudent.screens.main.attendance

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
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
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
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.ktor.client.HttpClient
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import wpustudent.composeapp.generated.resources.Res
import wpustudent.composeapp.generated.resources.baseline_done_24
import xyz.retrixe.wpustudent.R
import xyz.retrixe.wpustudent.api.erp.entities.CourseAttendanceSummary
import xyz.retrixe.wpustudent.api.erp.entities.THRESHOLD_PERCENTAGE
import xyz.retrixe.wpustudent.api.erp.entities.readableSubjectType
import xyz.retrixe.wpustudent.models.main.attendance.AttendanceViewModel
import xyz.retrixe.wpustudent.screens.Screens
import xyz.retrixe.wpustudent.state.LocalNavController
import xyz.retrixe.wpustudent.ui.components.FixedFractionIndicator
import xyz.retrixe.wpustudent.ui.components.PlainTooltipBox
import xyz.retrixe.wpustudent.utils.calculateClassesToThreshold
import xyz.retrixe.wpustudent.utils.calculateSkippableClasses
import xyz.retrixe.wpustudent.utils.getThresholdColor

@Composable
private fun LazyItemScope.AttendanceCard(course: CourseAttendanceSummary, threshold: Double) {
    val navController = LocalNavController.current
    val rawAttendance = course.present.toDouble() / course.total
    val attendance = rawAttendance * 100
    val color = getThresholdColor(attendance, threshold)

    OutlinedCard({
        val link = Screens.Main.Attendance.Details(course.id)
        navController.navigate(link)
    }, Modifier.animateItem().width(512.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(course.subjectName, fontSize = 24.sp)
            Text("(" + readableSubjectType(course.subjectType) + ")",
                color = MaterialTheme.colorScheme.outline, fontSize = 24.sp)
            Spacer(Modifier.height(16.dp))
            FixedFractionIndicator(Modifier.height(8.dp), rawAttendance, color)
            Spacer(Modifier.height(8.dp))
            Text(buildAnnotatedString {
                withStyle(style = SpanStyle(color = color, fontWeight = FontWeight.Bold)) {
                    append("%.2f".format(attendance) + "%")
                }
                val presentCount = course.present
                val totalSessions = course.total
                append(" ($presentCount / $totalSessions sessions)")
            })
            Spacer(Modifier.height(16.dp))
            // TODO: Estimate classes left, and how many one should attend
            if (attendance >= threshold - 5) {
                val skippableClassesSub = calculateSkippableClasses(
                    course.present, course.total, threshold - 5)
                Text("You can skip $skippableClassesSub classes and stay at ${(threshold - 5).toInt()}%.")
            } else {
                val classesToSubThreshold = calculateClassesToThreshold(
                    course.present, course.total, threshold - 5)
                Text("Attend $classesToSubThreshold classes to reach ${(threshold - 5).toInt()}%.")
            }
            if (attendance >= threshold) {
                val skippableClasses = calculateSkippableClasses(
                    course.present, course.total, threshold)
                Text("You can skip $skippableClasses classes and stay at ${threshold.toInt()}%.")
            } else {
                val classesToThreshold = calculateClassesToThreshold(
                    course.present, course.total, threshold)
                Text("Attend $classesToThreshold classes to reach ${threshold.toInt()}%.")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AttendanceScreen(
    paddingValues: PaddingValues,
    httpClient: HttpClient,
    attendanceThreshold: Double?,
) {
    val attendanceViewModelFactory = AttendanceViewModel.Factory(httpClient)
    val attendanceViewModel: AttendanceViewModel = viewModel(factory = attendanceViewModelFactory)
    val data by attendanceViewModel.data.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val refreshState = rememberPullToRefreshState()
    var refreshing by remember { mutableStateOf(false) }

    val filters = rememberSaveable { mutableStateSetOf<String>() }
    var sortedByAttendance by rememberSaveable { mutableStateOf(true) }

    fun refresh() = coroutineScope.launch {
        if (data is AttendanceViewModel.Data.Loading) return@launch
        refreshing = true
        attendanceViewModel.fetchData()
        refreshing = false
    }

    Column(Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(16.dp))
        Text("Attendance", fontSize = 36.sp)

        when (data) {
            is AttendanceViewModel.Data.Loading -> {
                Spacer(Modifier.weight(1f))
                LoadingIndicator(Modifier.size(96.dp).align(Alignment.CenterHorizontally))
                Spacer(Modifier.weight(1f))
            }

            is AttendanceViewModel.Data.Loaded -> {
                val unfilteredSummary = (data as AttendanceViewModel.Data.Loaded).summary
                val courseTypes = unfilteredSummary.map { it.subjectType }.toSortedSet {
                    a, b -> readableSubjectType(a).compareTo(readableSubjectType(b))
                }
                val summary =
                    if (filters.isEmpty()) unfilteredSummary
                    else unfilteredSummary.filter { it.subjectType in filters }

                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("Total", fontSize = 24.sp)
                    if (summary.isNotEmpty()) {
                        // Previously, when we were using PwC:
                        // summary.sumOf { it.presentCount * 100 / it.totalSessions } / summary.size
                        val totalAttendance =
                            summary.sumOf { it.present.toDouble() * 100 } / summary.sumOf { it.total }
                        val lowestThreshold =
                            attendanceThreshold ?: summary.minOf { THRESHOLD_PERCENTAGE + 5 }
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

                FlowRow(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                    val sortDesc =
                        if (sortedByAttendance) "Sort by Attendance" else "Sort Alphabetically"
                    PlainTooltipBox(sortDesc) {
                        IconButton(onClick = { sortedByAttendance = !sortedByAttendance }) {
                            Icon(painter = painterResource(
                                if (sortedByAttendance) R.drawable.baseline_sort_24
                                else R.drawable.baseline_sort_by_alpha_24
                            ), contentDescription = sortDesc)
                        }
                    }

                    for (type in courseTypes) {
                        FilterChip(
                            onClick = {
                                if (filters.contains(type))
                                    filters.remove(type)
                                else
                                    filters.add(type)
                            },
                            label = { Text(readableSubjectType(type)) },
                            selected = filters.contains(type),
                            leadingIcon = if (filters.contains(type)) {
                                {
                                    Icon(
                                        painterResource(Res.drawable.baseline_done_24),
                                        contentDescription = "${readableSubjectType(type)} icon",
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
                    LazyColumn(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val sortedItems =
                            if (sortedByAttendance)
                                summary.sortedBy { it.present.toDouble() / it.total }
                            else
                                summary.sortedBy { it.subjectName + it.subjectType }
                        items(
                            sortedItems,
                            key = { it.subjectName + it.subjectType + sortedByAttendance }
                        ) { course ->
                            AttendanceCard(course, attendanceThreshold ?: (THRESHOLD_PERCENTAGE + 5))
                        }
                    }
                }
            }

            else -> {
                Spacer(Modifier.weight(1f))
                Text("Failed to load attendance data!\n" +
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
