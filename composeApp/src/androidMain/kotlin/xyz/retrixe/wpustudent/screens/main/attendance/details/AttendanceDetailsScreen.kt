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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.ktor.client.HttpClient
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import wpustudent.composeapp.generated.resources.Res
import wpustudent.composeapp.generated.resources.baseline_arrow_back_24
import wpustudent.composeapp.generated.resources.baseline_done_24
import xyz.retrixe.wpustudent.api.erp.entities.StudentBasicInfo
import xyz.retrixe.wpustudent.api.erp.entities.THRESHOLD_PERCENTAGE
import xyz.retrixe.wpustudent.models.main.attendance.details.AttendanceDetailsViewModel
import xyz.retrixe.wpustudent.state.LocalNavController
import xyz.retrixe.wpustudent.ui.components.FixedFractionIndicator
import xyz.retrixe.wpustudent.ui.components.PlainTooltipBox
import xyz.retrixe.wpustudent.utils.DD_MM_YYYY_DATE
import xyz.retrixe.wpustudent.utils.RFC_1123_DATE
import xyz.retrixe.wpustudent.utils.SUCCESS_COLOR
import xyz.retrixe.wpustudent.utils.getThresholdColor
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AttendanceDetailsScreen(
    paddingValues: PaddingValues,
    httpClient: HttpClient,
    studentBasicInfo: StudentBasicInfo,
    attendanceThreshold: Double?,
) {
    val attendanceDetailsViewModelFactory = AttendanceDetailsViewModel.Factory(httpClient, studentBasicInfo)
    val attendanceDetailsViewModel: AttendanceDetailsViewModel = viewModel(factory = attendanceDetailsViewModelFactory)
    val data by attendanceDetailsViewModel.data.collectAsState()

    val navController = LocalNavController.current

    val coroutineScope = rememberCoroutineScope()
    val refreshState = rememberPullToRefreshState()
    var refreshing by remember { mutableStateOf(false) }

    val filters = rememberSaveable { mutableStateSetOf("ABSENT") }

    fun refresh() = coroutineScope.launch {
        if (data is AttendanceDetailsViewModel.Data.Loading) return@launch
        refreshing = true
        attendanceDetailsViewModel.fetchData()
        refreshing = false
    }

    Column(Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlainTooltipBox("Back to Attendance") {
                FilledTonalIconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        painterResource(Res.drawable.baseline_arrow_back_24),
                        contentDescription = "Back to Attendance"
                    )
                }
            }
            Text("Attendance Details", fontSize = 32.sp)
        }

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
                // TODO: Does this even look good here? You can see this in the summary.
                /* if (attendance >= threshold - 5) {
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
                Spacer(Modifier.height(16.dp)) */

                FlowRow(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                    for (filterName in arrayOf("Present", "Absent")) {
                        val filterId = filterName.uppercase()
                        FilterChip(
                            onClick = {
                                if (filters.contains(filterId))
                                    filters.remove(filterId)
                                else
                                    filters.add(filterId)
                            },
                            label = { Text(filterName) },
                            selected = filters.contains(filterId),
                            leadingIcon = if (filters.contains(filterId)) {
                                {
                                    Icon(
                                        painterResource(Res.drawable.baseline_done_24),
                                        contentDescription = "$filterName icon",
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
                        Modifier.fillMaxSize().padding(16.dp, 0.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val filteredDetails = (if (filters.isEmpty()) details
                                else details.filter { filters.contains(it.studentStatus) })
                            .sortedByDescending {
                                LocalDate.parse(it.attendanceDate, DD_MM_YYYY_DATE)
                            }
                        items(filteredDetails, key = { it }) { detail ->
                            OutlinedCard(Modifier.animateItem().width(512.dp)) {
                                Row(
                                    Modifier.padding(16.dp).fillMaxWidth(),
                                    Arrangement.SpaceBetween,
                                    Alignment.CenterVertically
                                ) {
                                    val date = LocalDate.parse(detail.attendanceDate, DD_MM_YYYY_DATE)
                                    val status = detail.studentStatus[0].uppercase() +
                                            detail.studentStatus.substring(1).lowercase()
                                    val color =
                                        if (status == "Present") SUCCESS_COLOR
                                        else MaterialTheme.colorScheme.error

                                    Text(RFC_1123_DATE.format(date),
                                        Modifier.weight(1f), fontSize = 20.sp, softWrap = true)
                                    Spacer(Modifier.width(16.dp))
                                    Text(status, color = color, fontSize = 16.sp, softWrap = false)
                                }
                            }
                        }
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
