package xyz.retrixe.wpustudent.screens.main.events

import androidx.compose.animation.Crossfade
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
import androidx.compose.material3.Badge
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.painterResource
import wpustudent.composeapp.generated.resources.Res
import wpustudent.composeapp.generated.resources.baseline_arrow_forward_24
import xyz.retrixe.wpustudent.api.erp.entities.Event
import xyz.retrixe.wpustudent.api.erp.entities.StudentBasicInfo
import xyz.retrixe.wpustudent.kmp.collectAsStateWithLifecycleMultiplatform
import xyz.retrixe.wpustudent.kmp.getAndroidContext
import xyz.retrixe.wpustudent.kmp.openCalendar
import xyz.retrixe.wpustudent.models.main.events.EventsViewModel
import xyz.retrixe.wpustudent.utils.RFC_1123_DATE
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
private fun EventCard(event: Event) {
    val context = getAndroidContext()
    val startDate = LocalDateTime.parse(event.startDate, LocalDateTime.Formats.ISO).date
    val endDate = LocalDateTime.parse(event.endDate, LocalDateTime.Formats.ISO).date

    OutlinedCard({ openCalendar(context, startDate) }, Modifier.width(512.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(event.name, fontSize = 24.sp)
            Spacer(Modifier.height(8.dp))
            Badge(containerColor = when (event.subType) {
                "Odd Semester" -> MaterialTheme.colorScheme.primaryContainer
                "Even Semester" -> MaterialTheme.colorScheme.primaryContainer
                "National Holiday" -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.secondaryContainer
            }) {
                Text(event.subType, Modifier.padding(2.dp))
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
            Spacer(Modifier.height(16.dp))
            Row(
                Modifier.fillMaxWidth(),
                Arrangement.spacedBy(ButtonDefaults.IconSpacing, Alignment.End)
            ) {
                Text("Open Calendar", fontWeight = FontWeight.Medium)
                Icon(painterResource(Res.drawable.baseline_arrow_forward_24), contentDescription = "Open calendar")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class, ExperimentalTime::class)
@Composable
fun EventsScreen(paddingValues: PaddingValues, studentBasicInfo: StudentBasicInfo) {
    val eventsViewModelFactory = EventsViewModel.Factory(studentBasicInfo)
    val eventsViewModel: EventsViewModel = viewModel(factory = eventsViewModelFactory)
    val data by eventsViewModel.data.collectAsStateWithLifecycleMultiplatform()

    val coroutineScope = rememberCoroutineScope()
    val refreshState = rememberPullToRefreshState()
    var refreshing by remember { mutableStateOf(false) }

    val tabs = listOf("Upcoming", "Past")
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    fun refresh() = coroutineScope.launch {
        if (data is EventsViewModel.Data.Loading) return@launch
        refreshing = true
        eventsViewModel.fetchData()
        refreshing = false
    }

    Column(Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(16.dp))
        Text("Events", fontSize = 36.sp)

        when (data) {
            is EventsViewModel.Data.Loading -> {
                Spacer(Modifier.weight(1f))
                LoadingIndicator(Modifier.size(96.dp).align(Alignment.CenterHorizontally))
                Spacer(Modifier.weight(1f))
            }

            is EventsViewModel.Data.Loaded -> {
                val events = (data as EventsViewModel.Data.Loaded).events

                Spacer(Modifier.height(16.dp))

                Row(
                    Modifier.padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                ) {
                    tabs.forEachIndexed { index, label ->
                        ToggleButton(
                            checked = selectedTab == index,
                            onCheckedChange = { selectedTab = index },
                            modifier = Modifier.weight(1f).semantics { role = Role.Tab },
                            shapes =
                                when (index) {
                                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                    tabs.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                },
                        ) {
                            Text(label)
                        }
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
                    val sortedEvents = events.sortedBy { it.startDate }
                    val pastEvents = sortedEvents.takeWhile {
                        LocalDateTime.parse(it.endDate, LocalDateTime.Formats.ISO).date <
                                Clock.System.todayIn(TimeZone.currentSystemDefault())
                    }
                    val upcomingEvents = sortedEvents
                        .takeLast(sortedEvents.size - pastEvents.size)
                    Crossfade(targetState = selectedTab, label = "Events") {
                        /* transitionSpec = {
                            if (targetState > initialState) {
                                slideInHorizontally { width -> width } + fadeIn() togetherWith
                                        slideOutHorizontally { width -> -width } + fadeOut()
                            } else {
                                slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                        slideOutHorizontally { height -> height } + fadeOut()
                            }.using(SizeTransform(clip = false))
                        } */
                        LazyColumn(
                            Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            /* if (upcomingEvents.isNotEmpty()) {
                                item {
                                    Text("Upcoming events", fontSize = 24.sp)
                                }
                            }
                            items(upcomingEvents) { event -> EventCard(event) }

                            if (pastEvents.isNotEmpty()) {
                                item {
                                    Text("Past events", fontSize = 24.sp)
                                }
                            } */
                            items(if (it == 0) upcomingEvents else pastEvents) { event ->
                                EventCard(event)
                            }
                        }
                    }
                }
            }

            else -> {
                Spacer(Modifier.weight(1f))
                Text("Failed to load events!\n" +
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
