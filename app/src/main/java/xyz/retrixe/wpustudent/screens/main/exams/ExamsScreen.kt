package xyz.retrixe.wpustudent.screens.main.exams

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
import xyz.retrixe.wpustudent.api.erp.entities.StudentInfo
import xyz.retrixe.wpustudent.api.pwc.entities.Exam
import xyz.retrixe.wpustudent.models.main.exams.ExamsViewModel
import xyz.retrixe.wpustudent.utils.RFC_1123_DATE
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
private fun ExamsCard(exam: Exam) {
    val date = LocalDate.parse(exam.examDate, DateTimeFormatter.ISO_LOCAL_DATE)
    OutlinedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(exam.courseName, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(exam.courseCode, color = MaterialTheme.colorScheme.outline, fontSize = 24.sp)
            Spacer(Modifier.height(8.dp))
            Badge(containerColor = when (exam.examTypeCode) {
                "SUPPL" -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.primaryContainer
            }) {
                Text(when (exam.examTypeCode) {
                    "REG" -> "Regular"
                    "SUPPL" -> "Backlog"
                    else -> exam.examTypeCode
                }, Modifier.padding(2.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text(RFC_1123_DATE.format(date), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(exam.time, fontSize = 20.sp)
            if (exam.eligibility != "Yes") {
                Spacer(Modifier.height(16.dp))
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Note: ") }
                        append("You are ineligible for this exam!")
                    },
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 20.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamsScreen(
    paddingValues: PaddingValues,
    httpClient: HttpClient,
    studentInfo: StudentInfo
) {
    val examsViewModelFactory = ExamsViewModel.Factory(httpClient, studentInfo)
    val examsViewModel: ExamsViewModel = viewModel(factory = examsViewModelFactory)
    val data by examsViewModel.data.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val refreshState = rememberPullToRefreshState()
    var refreshing by remember { mutableStateOf(false) }

    fun refresh() = coroutineScope.launch {
        if (data is ExamsViewModel.Data.Loading) return@launch
        refreshing = true
        examsViewModel.fetchData()
        refreshing = false
    }

    Column(Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(16.dp))
        Text("Exams", fontSize = 36.sp, fontWeight = FontWeight.Bold)

        when (data) {
            is ExamsViewModel.Data.Loading -> {
                Spacer(Modifier.weight(1f))
                CircularProgressIndicator(Modifier.size(96.dp).align(Alignment.CenterHorizontally))
                Spacer(Modifier.weight(1f))
            }

            is ExamsViewModel.Data.Loaded -> {
                val data = (data as ExamsViewModel.Data.Loaded).data

                Text(data.sessionName,
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 24.sp)
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
                        val sortedExams = data.ticket.flatten().sortedBy { it.examDate }
                        val completedExams = sortedExams.takeWhile {
                            LocalDate
                                .parse(it.examDate, DateTimeFormatter.ISO_LOCAL_DATE)
                                .isBefore(LocalDate.now())
                        }
                        val upcomingExams = sortedExams
                            .takeLast(sortedExams.size - completedExams.size)

                        item {
                            if (upcomingExams.isNotEmpty()) {
                                Text("Upcoming exams",
                                    fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(16.dp))
                            }
                        }
                        items(upcomingExams) { exam ->
                            ExamsCard(exam)
                            Spacer(Modifier.height(16.dp))
                        }

                        item {
                            if (completedExams.isNotEmpty()) {
                                if (upcomingExams.isNotEmpty()) {
                                    Spacer(Modifier.height(16.dp))
                                }
                                Text("Completed exams",
                                    fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(16.dp))
                            }
                        }
                        items(completedExams) { exam ->
                            ExamsCard(exam)
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
            }

            else -> {
                Spacer(Modifier.weight(1f))
                Text("Failed to load exams!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.weight(1f))
            }
        }
    }
}
