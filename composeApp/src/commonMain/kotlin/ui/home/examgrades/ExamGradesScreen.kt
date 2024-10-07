package ui.home.examgrades

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dokar.sonner.ToasterState
import dev.icerock.moko.resources.compose.stringResource
import dev.materii.pullrefresh.PullRefreshLayout
import dev.materii.pullrefresh.rememberPullRefreshState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import mehiz.abdallah.progres.domain.models.AcademicPeriodModel
import mehiz.abdallah.progres.domain.models.ExamGradeModel
import mehiz.abdallah.progres.i18n.MR
import org.koin.compose.koinInject
import presentation.ErrorScreenContent
import presentation.MaterialPullRefreshIndicator
import presentation.errorToast
import ui.home.ccgrades.PeriodPlusAcademicYearText

object ExamGradesScreen : Screen {

  override val key = uniqueScreenKey

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val screenModel = koinScreenModel<ExamGradesScreenModel>()
    val examGrades by screenModel.examGrades.collectAsState()
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
            Text(stringResource(MR.strings.home_exams_results))
          },
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
        state = ptrState,
        modifier = Modifier.padding(paddingValues),
        indicator = { MaterialPullRefreshIndicator(ptrState) },
      ) {
        examGrades.DisplayResult(
          onLoading = { LinearProgressIndicator(Modifier.fillMaxWidth()) },
          onSuccess = { ExamGradesScreenContent(it) },
          onError = { ErrorScreenContent(it) },
        )
      }
    }
  }

  @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
  @Composable
  fun ExamGradesScreenContent(
    examGrades: ImmutableMap<AcademicPeriodModel, List<ExamGradeModel>>,
    modifier: Modifier = Modifier,
  ) {
    val scope = rememberCoroutineScope()
    Column(modifier) {
      val pagerState = rememberPagerState(examGrades.keys.size - 1) { examGrades.keys.size }
      PrimaryScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        divider = {},
      ) {
        repeat(pagerState.pageCount) { index ->
          val period = examGrades.keys.elementAt(index)
          Tab(
            index == pagerState.currentPage,
            onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
            text = { PeriodPlusAcademicYearText(period.periodStringLatin, period.academicYearStringLatin) },
          )
        }
      }
      HorizontalDivider()
      HorizontalPager(
        pagerState,
        modifier = Modifier
          .fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 0.dp),
        pageSpacing = 16.dp,
        verticalAlignment = Alignment.Top,
        beyondBoundsPageCount = 1,
      ) { currentPage ->
        val currentSemesterExams = remember {
          examGrades.getValue(examGrades.keys.elementAt(currentPage))
        }
        val resiteSession = remember {
          derivedStateOf {
            currentSemesterExams.filterNot { it.sessionTitle != "rattrappage" }
          }
        }
        val normalSession = remember {
          derivedStateOf { currentSemesterExams.minus(resiteSession.value.toSet()) }
        }
        Column(
          verticalArrangement = Arrangement.spacedBy(16.dp),
          modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        ) {
          ExamGradesCollection(
            title = stringResource(MR.strings.exam_grades_normal_session),
            examGrades = normalSession.value.toImmutableList(),
          )
          if (resiteSession.value.isNotEmpty()) {
            ExamGradesCollection(
              title = stringResource(MR.strings.exam_grades_resite_session),
              examGrades = resiteSession.value.toImmutableList(),
            )
          }
        }
      }
    }
  }

  @Composable
  fun ExamGradesCollection(
    title: String,
    examGrades: ImmutableList<ExamGradeModel>,
    modifier: Modifier = Modifier,
  ) {
    var expanded by remember { mutableStateOf(true) }
    val rotationState by animateFloatAsState(
      targetValue = if (expanded) 180f else 0f,
      label = "arrow_rotation",
    )
    Card(
      modifier = modifier
        .fillMaxWidth()
        .animateContentSize(animationSpec = tween(durationMillis = 300)),
      onClick = { expanded = !expanded },
    ) {
      Row(
        modifier = Modifier.padding(start = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          modifier = Modifier.weight(6f),
          text = title,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        IconButton(
          modifier = Modifier
            .weight(1f)
            .alpha(0.4f)
            .rotate(rotationState),
          onClick = { expanded = !expanded },
        ) { Icon(Icons.Rounded.ArrowDropDown, null) }
      }
      AnimatedVisibility(
        expanded,
        enter = expandVertically { -it },
        exit = shrinkVertically { -it },
      ) {
        Column {
          ReportCardHeader()
          ExamGrades(examGrades)
        }
      }
    }
  }

  @Composable
  fun ReportCardHeader(
    modifier: Modifier = Modifier,
  ) {
    Row(
      modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp),
      horizontalArrangement = Arrangement.Absolute.SpaceBetween,
    ) {
      Text(
        stringResource(MR.strings.exam_grades_header_subject),
        modifier = Modifier.weight(3.5f),
        fontSize = 12.sp,
      )
      Text(
        stringResource(MR.strings.exam_grades_header_credit),
        modifier = Modifier.weight(1f),
        fontSize = 12.sp,
      )
      Text(
        stringResource(MR.strings.exam_grades_header_coef),
        modifier = Modifier.weight(1f),
        fontSize = 12.sp,
      )
      Text(
        stringResource(MR.strings.exam_grades_header_grade),
        modifier = Modifier.weight(1.5f),
        fontSize = 12.sp,
        textAlign = TextAlign.End,
      )
    }
  }

  @Composable
  fun ExamGrades(
    examGrades: ImmutableList<ExamGradeModel>,
    modifier: Modifier = Modifier,
  ) {
    Column(modifier) {
      examGrades.forEach {
        ExamGrade(subject = it)
      }
    }
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Composable
  fun ExamGrade(
    subject: ExamGradeModel,
    modifier: Modifier = Modifier,
  ) {
    Row(
      modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Text(
        subject.subjectLabelLatin,
        fontSize = 12.sp,
        maxLines = 1,
        modifier = Modifier.weight(3.5f).basicMarquee(),
      )
      Text(
        stringResource(MR.strings.generic_float, subject.credit),
        fontSize = 12.sp,
        modifier = Modifier.weight(1f),
      )
      Text(
        stringResource(MR.strings.generic_float, subject.coefficent),
        fontSize = 12.sp,
        modifier = Modifier.weight(1f),
      )
      Text(
        stringResource(MR.strings.grade, subject.grade ?: 0, 20),
        fontSize = 12.sp,
        modifier = Modifier.weight(1.5f),
        textAlign = TextAlign.End,
        color = if ((subject.grade ?: 0.0) < 10.0) {
          MaterialTheme.colorScheme.error
        } else {
          MaterialTheme.colorScheme.primary
        },
      )
    }
  }
}
