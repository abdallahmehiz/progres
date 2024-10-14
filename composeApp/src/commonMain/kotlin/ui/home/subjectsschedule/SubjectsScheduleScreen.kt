package ui.home.subjectsschedule

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Title
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dokar.sonner.ToasterState
import com.svenjacobs.reveal.Reveal
import com.svenjacobs.reveal.RevealCanvasState
import com.svenjacobs.reveal.RevealOverlayArrangement
import com.svenjacobs.reveal.RevealOverlayScope
import com.svenjacobs.reveal.RevealState
import com.svenjacobs.reveal.rememberRevealState
import com.svenjacobs.reveal.revealable
import com.svenjacobs.reveal.shapes.balloon.Arrow
import com.svenjacobs.reveal.shapes.balloon.Balloon
import dev.icerock.moko.resources.compose.stringResource
import dev.materii.pullrefresh.PullRefreshLayout
import dev.materii.pullrefresh.rememberPullRefreshState
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import mehiz.abdallah.progres.domain.algerianDayNumber
import mehiz.abdallah.progres.domain.algerianSortedDayOfWeek
import mehiz.abdallah.progres.domain.models.AcademicPeriodModel
import mehiz.abdallah.progres.domain.models.SubjectScheduleModel
import mehiz.abdallah.progres.i18n.MR
import org.koin.compose.koinInject
import presentation.ErrorScreenContent
import presentation.MaterialPullRefreshIndicator
import presentation.NoDataScreen
import presentation.ScreenHeightDp
import presentation.TimeTableEventData
import presentation.TimeTableWithGrid
import presentation.errorToast
import ui.home.ccgrades.PeriodPlusAcademicYearText
import utils.FirebaseUtils
import utils.isNetworkError

object SubjectsScheduleScreen : Screen {
  override val key = uniqueScreenKey

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val screenModel = koinScreenModel<SubjectsScheduleScreenModel>()
    val schedule by screenModel.schedule.collectAsState()
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
            if (!e.isNetworkError) FirebaseUtils.reportException(e)
            toasterState.show(errorToast(e.message!!))
          }
          isRefreshing = false
        }
      },
    )
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text(stringResource(MR.strings.home_time_table)) },
          navigationIcon = {
            IconButton(onClick = navigator::pop) {
              Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
            }
          },
          windowInsets = WindowInsets(0.dp),
        )
      },
    ) { paddingValues ->
      PullRefreshLayout(
        ptrState,
        modifier = Modifier.padding(paddingValues),
        indicator = { MaterialPullRefreshIndicator(ptrState) },
      ) {
        schedule.DisplayResult(
          onLoading = { LinearProgressIndicator(Modifier.fillMaxWidth()) },
          onSuccess = { if (it.isNotEmpty()) SubjectsScheduleScreenContent(it) else NoDataScreen() },
          onError = { ErrorScreenContent(it) },
        )
      }
    }
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Composable
  fun SubjectsScheduleScreenContent(
    schedule: ImmutableMap<AcademicPeriodModel?, List<SubjectScheduleModel>>,
    modifier: Modifier = Modifier,
  ) {
    val scope = rememberCoroutineScope()
    val periodPagerState = rememberPagerState(schedule.keys.size - 1) { schedule.keys.size }
    val revealCanvasState = koinInject<RevealCanvasState>()
    Column(modifier) {
      Row(
        modifier = Modifier.padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        IconButton(
          enabled = periodPagerState.currentPage > 0,
          onClick = { scope.launch { periodPagerState.animateScrollToPage(periodPagerState.currentPage - 1) } },
        ) { Icon(Icons.AutoMirrored.Rounded.ArrowBackIos, null) }
        HorizontalPager(
          periodPagerState,
          modifier = Modifier.weight(1f),
        ) {
          PeriodPlusAcademicYearText(
            schedule.keys.elementAt(it)?.periodStringLatin ?: "",
            schedule.keys.elementAt(it)?.academicYearStringLatin ?: "",
          )
        }
        IconButton(
          enabled = periodPagerState.currentPage < periodPagerState.pageCount - 1,
          onClick = { scope.launch { periodPagerState.animateScrollToPage(periodPagerState.currentPage + 1) } },
        ) { Icon(Icons.AutoMirrored.Rounded.ArrowForwardIos, null) }
      }
      var currentSchedule by remember {
        mutableStateOf(
          schedule[schedule.keys.elementAt(periodPagerState.currentPage)]!!,
        )
      }
      LaunchedEffect(periodPagerState.currentPage) {
        currentSchedule = schedule[schedule.keys.elementAt(periodPagerState.currentPage)]!!
      }
      val revealState = rememberRevealState()
      Reveal(
        revealCanvasState,
        revealState = revealState,
        onOverlayClick = { scope.launch { revealState.hide() } },
        overlayContent = { id ->
          SubjectScheduleOverlayContent(
            currentSchedule.first { it.id == id },
            modifier = Modifier.widthIn(max = 200.dp),
          )
        },
        onRevealableClick = { scope.launch { revealState.reveal(it) } },
      ) {
        val days = currentSchedule.mapNotNull { it.day }.distinct().sortedBy { it.algerianDayNumber }.toImmutableList()
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
          TimeTableWithGrid(
            startHour = startHour,
            endHour = endHour,
            events = currentSchedule.mapNotNull {
              it.toTimeTableEvent(
                revealState = revealState,
                onClick = { scope.launch { revealState.reveal(it.id) } },
              )
            }.toImmutableList(),
            days = if (days.isEmpty()) algerianSortedDayOfWeek else days,
            hourHeight = ScreenHeightDp() / 10,
          )
        }
      }
    }
  }
}

val startHour = LocalTime(8, 0)
val endHour = LocalTime(18, 0)

@Composable
fun SubjectScheduleModel.toTimeTableEvent(
  onClick: (SubjectScheduleModel) -> Unit,
  revealState: RevealState,
): TimeTableEventData? {
  if (hourlyRangeStart == null || hourlyRangeStart == null || day == null) return null
  return TimeTableEventData(
    startTime = hourlyRangeStart!!,
    endTime = hourlyRangeEnd!!,
    day = day!!,
    content = { SubjectScheduleEvent(this, revealState, onClick) },
  )
}

@Composable
fun SubjectScheduleEvent(
  subjectModel: SubjectScheduleModel,
  revealState: RevealState,
  onClick: (SubjectScheduleModel) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .revealable(subjectModel.id, revealState)
      .padding(2.dp)
      .clip(RoundedCornerShape(8.dp))
      .clickable(onClick = { onClick(subjectModel) })
      .background(subjectBackgroundColor(subjectModel.ap))
      .padding(2.dp),
  ) {
    CompositionLocalProvider(LocalContentColor provides subjectTextColor(subjectModel.ap)) {
      Column {
        Text(
          subjectModel.ap,
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.ExtraBold,
        )
        Text(
          subjectModel.groupString,
          fontSize = 10.sp,
          lineHeight = 10.sp,
          fontWeight = FontWeight.Bold,
        )
        Text(
          subjectModel.subjectStringLatin,
          fontSize = 10.sp,
          lineHeight = 10.sp,
        )
      }
    }
  }
}

@Composable
fun RevealOverlayScope.SubjectScheduleOverlayContent(
  model: SubjectScheduleModel,
  modifier: Modifier = Modifier,
) {
  val vAlignment = if ((model.hourlyRangeStart?.hour ?: 0) > 11) {
    RevealOverlayArrangement.Top
  } else {
    RevealOverlayArrangement.Bottom
  }
  val hAlignment = if ((model.day?.algerianDayNumber ?: 3) > DayOfWeek.WEDNESDAY.algerianDayNumber) {
    RevealOverlayArrangement.Start
  } else {
    null
  }
  Balloon(
    arrow = when {
      hAlignment != null -> Arrow.end()
      else -> if (vAlignment == RevealOverlayArrangement.Top) Arrow.bottom() else Arrow.top()
    },
    backgroundColor = MaterialTheme.colorScheme.surfaceBright,
    modifier = modifier
      .then(if (hAlignment != null) Modifier.align(hAlignment) else Modifier.align(vAlignment)),
  ) {
    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
      Column {
        ScheduleDataNode(
          Icons.Rounded.Info,
          stringResource(MR.strings.subjects_schedule_type),
          model.ap,
        )
        ScheduleDataNode(
          Icons.Rounded.Title,
          stringResource(MR.strings.subjects_schedule_subject),
          model.subjectStringLatin,
        )
        ScheduleDataNode(
          Icons.Rounded.Groups,
          stringResource(MR.strings.groups_group),
          model.groupString,
        )
        if (model.teacherFirstNameLatin != null && model.teacherLastNameLatin != null) {
          ScheduleDataNode(
            Icons.Rounded.School,
            stringResource(MR.strings.subjects_schedule_teacher),
            model.teacherFirstNameLatin + ' ' + model.teacherLastNameLatin,
          )
        }
        if (model.locationDesignation != null) {
          ScheduleDataNode(
            Icons.Rounded.LocationOn,
            stringResource(MR.strings.subjects_schedule_location),
            model.locationDesignation!!,
          )
        }
        if (model.hourlyRangeStringLatin != null) {
          ScheduleDataNode(
            Icons.Rounded.Schedule,
            stringResource(MR.strings.subjects_schedule_hourly_range),
            model.hourlyRangeStringLatin!!,
          )
        }
      }
    }
  }
}

@Composable
fun ScheduleDataNode(
  icon: ImageVector,
  title: String,
  data: String,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier.padding(4.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    Icon(icon, null)
    Column {
      Text(
        title,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
      )
      Text(
        data,
        style = MaterialTheme.typography.labelMedium,
        overflow = TextOverflow.Ellipsis,
        maxLines = 2,
      )
    }
  }
}

@Composable
inline fun subjectTextColor(ap: String) = when (ap) {
  "TD" -> MaterialTheme.colorScheme.onTertiaryContainer
  "TP" -> MaterialTheme.colorScheme.onSecondaryContainer
  "CM" -> MaterialTheme.colorScheme.onPrimaryContainer
  else -> MaterialTheme.colorScheme.onErrorContainer
}

@Composable
inline fun subjectBackgroundColor(ap: String) = when (ap) {
  "TD" -> MaterialTheme.colorScheme.tertiaryContainer
  "TP" -> MaterialTheme.colorScheme.secondaryContainer
  "CM" -> MaterialTheme.colorScheme.primaryContainer
  else -> MaterialTheme.colorScheme.errorContainer
}
