package ui.home.examsschedule

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dokar.sonner.ToasterState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.plusMonths
import com.kizitonwose.calendar.core.yearMonth
import dev.icerock.moko.resources.compose.pluralStringResource
import dev.icerock.moko.resources.compose.stringResource
import dev.materii.pullrefresh.PullRefreshLayout
import dev.materii.pullrefresh.rememberPullRefreshState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import mehiz.abdallah.progres.domain.models.AcademicPeriodModel
import mehiz.abdallah.progres.domain.models.ExamScheduleModel
import mehiz.abdallah.progres.i18n.MR
import org.koin.compose.koinInject
import presentation.ErrorScreenContent
import presentation.MaterialPullRefreshIndicator
import presentation.errorToast
import ui.home.ccgrades.PeriodPlusAcademicYearText
import kotlin.math.abs

object ExamsScheduleScreen : Screen {

  override val key = uniqueScreenKey

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val screenModel = koinScreenModel<ExamsScheduleScreenModel>()
    val examSchedules by screenModel.examSchedules.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    val toasterState = koinInject<ToasterState>()
    val ptrState = rememberPullRefreshState(
      refreshing = isRefreshing,
      onRefresh = {
        isRefreshing = true
        screenModel.screenModelScope.launch(Dispatchers.IO) {
          try {
            screenModel.refresh()
          } catch (e: Exception) {
            toasterState.show(errorToast(e.message!!))
          }
          isRefreshing = false
        }
      },
    )
    Scaffold(
      topBar = {
        TopAppBar(
          title = {
            Text(stringResource(MR.strings.home_exams_schedule))
          },
          navigationIcon = {
            IconButton(onClick = navigator::pop) {
              Icon(Icons.AutoMirrored.Default.ArrowBack, null)
            }
          },
          windowInsets = WindowInsets(0.dp),
        )
      },
    ) { paddingValues ->
      PullRefreshLayout(
        state = ptrState,
        modifier = Modifier.padding(paddingValues),
        indicator = { MaterialPullRefreshIndicator(ptrState) },
      ) {
        examSchedules.DisplayResult(
          onLoading = { LinearProgressIndicator(Modifier.fillMaxWidth()) },
          onSuccess = { ExamsScheduleScreenContent(it) },
          onError = { ErrorScreenContent(it) },
        )
      }
    }
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Composable
  fun ExamsScheduleScreenContent(
    examSchedules: ImmutableMap<AcademicPeriodModel, List<ExamScheduleModel>>,
    modifier: Modifier = Modifier,
  ) {
    val today = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }
    val scope = rememberCoroutineScope()
    Column(modifier) {
      var currentSemester by remember { mutableStateOf(examSchedules.keys.last()) }
      val currentSemesterExams by remember {
        derivedStateOf { examSchedules[currentSemester]!!.sortedByDescending { it.examDate.dayOfYear } }
      }
      val currentMonth by remember { derivedStateOf { currentSemesterExams.first().examDate.date.yearMonth } }
      val calendarState = rememberCalendarState(
        firstVisibleMonth = currentMonth,
        startMonth = currentMonth.minusMonths(100 * 12),
        endMonth = currentMonth.plusMonths(100 * 12),
      )
      val semesterPagerState = rememberPagerState(initialPage = examSchedules.keys.size - 1) {
        examSchedules.keys.size
      }
      var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
      LaunchedEffect(semesterPagerState.currentPage) {
        currentSemester = examSchedules.keys.elementAt(semesterPagerState.currentPage)
        calendarState.animateScrollToMonth(currentSemesterExams.first().examDate.date.yearMonth)
      }

      val lazyListState = rememberLazyListState()
      LaunchedEffect(selectedDate) {
        if (selectedDate != null && currentSemesterExams.any { it.examDate.date == selectedDate }) {
          lazyListState.animateScrollToItem(currentSemesterExams.indexOfFirst { it.examDate.date == selectedDate })
        }
      }
      HorizontalCalendar(
        state = calendarState,
        userScrollEnabled = true,
        modifier = Modifier.fillMaxWidth(),
        monthHeader = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              stringResource(
                MR.strings.exams_schedule_month_year_formatted,
                stringResource(abbreviatedMonthStringResources[it.yearMonth.month]!!),
                it.yearMonth.year,
              ),
              modifier = Modifier.padding(start = 16.dp).padding(vertical = 8.dp),
              style = MaterialTheme.typography.headlineMedium,
            )
            Row(
              modifier = Modifier.padding(horizontal = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              IconButton(
                onClick = {
                  scope.launch {
                    semesterPagerState.animateScrollToPage(page = (semesterPagerState.currentPage - 1).coerceAtLeast(0))
                  }
                },
                enabled = semesterPagerState.currentPage != 0,
              ) {
                Icon(Icons.AutoMirrored.Default.ArrowBackIos, null)
              }
              HorizontalPager(
                semesterPagerState,
                modifier = Modifier.weight(1f),
              ) {
                val period = examSchedules.keys.elementAt(it)
                PeriodPlusAcademicYearText(
                  period.periodStringLatin,
                  period.academicYearStringLatin,
                  modifier = Modifier
                    .fillMaxWidth(),
                )
              }
              IconButton(
                onClick = {
                  scope.launch {
                    semesterPagerState
                      .animateScrollToPage(
                        page = (semesterPagerState.currentPage + 1).coerceAtLeast(0),
                      )
                  }
                },
                enabled = semesterPagerState.currentPage != examSchedules.keys.size - 1,
              ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowForwardIos, null)
              }
            }
          }

          DaysOfWeekTitle(
            it.weekDays.first().map { it.date.dayOfWeek }.toImmutableList(),
          )
        },
        dayContent = { date ->
          Day(
            date = date,
            events = currentSemesterExams.count { it.examDate.date == date.date },
            isSelected = selectedDate == date.date,
            isToday = today.date == date.date,
            hasPassed = today.date < date.date,
            onClick = { _ -> selectedDate = date.date },
          )
        },
      )
      HorizontalDivider()
      LazyColumn(state = lazyListState) {
        items(currentSemesterExams) {
          ExamScheduleDetailsItem(
            exam = it,
            isSelected = it.examDate.date == selectedDate,
            onClick = { _ -> selectedDate = it.examDate.date },
          )
        }
      }
    }
  }
}

@Composable
fun DaysOfWeekTitle(
  daysOfWeek: ImmutableList<DayOfWeek>,
  modifier: Modifier = Modifier,
) {
  Row(modifier = modifier.fillMaxWidth()) {
    for (dayOfWeek in daysOfWeek) {
      Text(
        text = stringResource(abbreviatedDayOfWeekStringResources[dayOfWeek]!!),
        modifier = Modifier.weight(1f),
        textAlign = TextAlign.Center,
      )
    }
  }
}

@Composable
fun Day(
  date: CalendarDay,
  events: Int,
  isSelected: Boolean,
  isToday: Boolean,
  hasPassed: Boolean,
  modifier: Modifier = Modifier,
  onClick: (CalendarDay) -> Unit = {},
) {
  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center,
  ) {
    if (isSelected) {
      Box(Modifier.size(40.dp).border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)))
    }
    Column(
      modifier = Modifier
        .aspectRatio(1.3f)
        .clickable { onClick(date) },
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      Box(contentAlignment = Alignment.Center) {
        if (isToday) {
          Box(Modifier.size(24.dp).aspectRatio(1f).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
        }
        Text(
          date.date.dayOfMonth.toString(),
          fontWeight = FontWeight.Bold,
          color = if (isToday) {
            MaterialTheme.colorScheme.onPrimary
          } else {
            MaterialTheme.colorScheme.onSurface.copy(if (hasPassed) .5f else 1f)
          },
        )
      }
      Spacer(Modifier.height(2.dp))
      Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        for (i in 1..events.coerceAtMost(3)) {
          Box(
            modifier = Modifier.size(6.dp).clip(CircleShape)
              .background(MaterialTheme.colorScheme.primary.copy(1f / i)),
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExamScheduleDetailsItem(
  exam: ExamScheduleModel,
  modifier: Modifier = Modifier,
  isSelected: Boolean = false,
  onClick: (LocalDate) -> Unit,
) {
  val localDate = exam.examDate
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = modifier.fillMaxWidth().background(
      if (isSelected) {
        MaterialTheme.colorScheme.primary
      } else {
        MaterialTheme.colorScheme.background
      },
    )
      .clickable { onClick(exam.examDate.date) }
      .padding(horizontal = 16.dp, vertical = 8.dp),
  ) {
    Column(
      modifier = Modifier.weight(0.65f),
    ) {
      Text(
        exam.subjectStringLatin,
        fontWeight = FontWeight.Bold,
        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
        maxLines = 1,
        modifier = Modifier.basicMarquee(),
      )
      Text(
        stringResource(
          MR.strings.formatted_date,
          stringResource(abbreviatedDayOfWeekStringResources[exam.examDate.dayOfWeek]!!),
          localDate.dayOfMonth,
          stringResource(abbreviatedMonthStringResources[localDate.month]!!),
          localDate.year,
        ),
        color = if (isSelected) {
          MaterialTheme.colorScheme.onPrimary.copy(.7f)
        } else {
          MaterialTheme.colorScheme.onBackground.copy(.7f)
        },
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
    Column(
      modifier = Modifier.weight(0.35f),
      horizontalAlignment = Alignment.End,
    ) {
      val daysLeft = Clock.System.now().daysUntil(
        exam.examDate.toInstant(TimeZone.currentSystemDefault()),
        TimeZone.currentSystemDefault(),
      )
      Text(
        pluralStringResource(
          if (daysLeft < 0) MR.plurals.days_ago else MR.plurals.days_left,
          abs(daysLeft),
          abs(daysLeft),
        ),
        color = if (isSelected) {
          MaterialTheme.colorScheme.onPrimary.copy(.9f)
        } else {
          MaterialTheme.colorScheme.onBackground
        },
      )
      Text(
        "${exam.examStartHour.formattedHHmm()} - ${exam.duration}'",
        color = if (isSelected) {
          MaterialTheme.colorScheme.tertiaryContainer
        } else {
          MaterialTheme.colorScheme.onBackground.copy(0.5f)
        },
      )
    }
  }
}

@OptIn(FormatStringsInDatetimeFormats::class)
inline fun LocalDateTime.formattedHHmm(): String = format(LocalDateTime.Format { byUnicodePattern("HH:mm") })

val abbreviatedDayOfWeekStringResources = mapOf(
  DayOfWeek.MONDAY to MR.strings.day_monday_abbreviated,
  DayOfWeek.TUESDAY to MR.strings.day_tuesday_abbreviated,
  DayOfWeek.WEDNESDAY to MR.strings.day_wednesday_abbreviated,
  DayOfWeek.THURSDAY to MR.strings.day_thursday_abbreviated,
  DayOfWeek.FRIDAY to MR.strings.day_friday_abbreviated,
  DayOfWeek.SATURDAY to MR.strings.day_saturday_abbreviated,
  DayOfWeek.SUNDAY to MR.strings.day_sunday_abbreviated,
)

val dayOfWeekStringResources = mapOf(
  DayOfWeek.MONDAY to MR.strings.day_monday,
  DayOfWeek.TUESDAY to MR.strings.day_tuesday,
  DayOfWeek.WEDNESDAY to MR.strings.day_wednesday,
  DayOfWeek.THURSDAY to MR.strings.day_thursday,
  DayOfWeek.FRIDAY to MR.strings.day_friday,
  DayOfWeek.SATURDAY to MR.strings.day_saturday,
  DayOfWeek.SUNDAY to MR.strings.day_sunday,
)

val abbreviatedMonthStringResources = mapOf(
  Month.JANUARY to MR.strings.month_january_abbreviated,
  Month.FEBRUARY to MR.strings.month_february_abbreviated,
  Month.MARCH to MR.strings.month_march_abbreviated,
  Month.APRIL to MR.strings.month_april_abbreviated,
  Month.MAY to MR.strings.month_may_abbreviated,
  Month.JUNE to MR.strings.month_june_abbreviated,
  Month.JULY to MR.strings.month_july_abbreviated,
  Month.AUGUST to MR.strings.month_august_abbreviated,
  Month.SEPTEMBER to MR.strings.month_september_abbreviated,
  Month.OCTOBER to MR.strings.month_october_abbreviated,
  Month.NOVEMBER to MR.strings.month_november_abbreviated,
  Month.DECEMBER to MR.strings.month_december_abbreviated,
)

val monthStringResources = mapOf(
  Month.JANUARY to MR.strings.month_january,
  Month.FEBRUARY to MR.strings.month_february,
  Month.MARCH to MR.strings.month_march,
  Month.APRIL to MR.strings.month_april,
  Month.MAY to MR.strings.month_may,
  Month.JUNE to MR.strings.month_june,
  Month.JULY to MR.strings.month_july,
  Month.AUGUST to MR.strings.month_august,
  Month.SEPTEMBER to MR.strings.month_september,
  Month.OCTOBER to MR.strings.month_october,
  Month.NOVEMBER to MR.strings.month_november,
  Month.DECEMBER to MR.strings.month_december,
)
