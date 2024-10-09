package ui.home.subjects

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
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
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import mehiz.abdallah.progres.domain.models.AcademicPeriodModel
import mehiz.abdallah.progres.domain.models.SubjectModel
import mehiz.abdallah.progres.i18n.MR
import org.koin.compose.koinInject
import presentation.ErrorScreenContent
import presentation.MaterialPullRefreshIndicator
import presentation.NoDataScreen
import presentation.errorToast
import ui.home.ccgrades.PeriodPlusAcademicYearText

object SubjectsScreen : Screen {
  override val key = uniqueScreenKey

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val screenModel = koinScreenModel<SubjectsScreenModel>()
    val subjects by screenModel.subjects.collectAsState()
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
            Text(stringResource(MR.strings.home_subjects))
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
        subjects.DisplayResult(
          onLoading = { LinearProgressIndicator(Modifier.fillMaxWidth()) },
          onSuccess = { if (it.isNotEmpty()) SubjectsScreenContent(it) else NoDataScreen() },
          onError = { ErrorScreenContent(it) },
        )
      }
    }
  }

  @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
  @Composable
  fun SubjectsScreenContent(
    subjects: ImmutableMap<AcademicPeriodModel, List<SubjectModel>>,
    modifier: Modifier = Modifier,
  ) {
    val scope = rememberCoroutineScope()
    val yearPagerState = rememberPagerState(subjects.keys.size - 1) { subjects.keys.size }
    Column(modifier) {
      PrimaryScrollableTabRow(
        yearPagerState.currentPage,
        divider = {},
      ) {
        subjects.keys.forEachIndexed { index, year ->
          Tab(
            year == subjects.keys.elementAt(yearPagerState.currentPage),
            onClick = { scope.launch { yearPagerState.animateScrollToPage(index) } },
            text = {
              PeriodPlusAcademicYearText(
                year.periodStringLatin,
                year.academicYearStringLatin,
              )
            },
          )
        }
      }
      HorizontalDivider()
      HorizontalPager(
        state = yearPagerState
      ) { year ->
        val currentPeriodSubjects by remember { mutableStateOf(subjects[subjects.keys.elementAt(year)]!!) }
        LazyColumn(
          modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          item { Spacer(Modifier.height(8.dp)) }
          items(currentPeriodSubjects) { SubjectPercentage(it) }
          item { Spacer(Modifier.height(8.dp)) }
        }
      }
    }
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Composable
  fun SubjectPercentage(
    subject: SubjectModel,
    modifier: Modifier = Modifier,
  ) {
    Column(
      modifier = modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 4.dp, bottomStart = 4.dp))
          .background(MaterialTheme.colorScheme.surfaceContainerHighest)
          .padding(horizontal = 8.dp, vertical = 4.dp),
      ) {
        Text(
          text = subject.subjectStringLatin,
          maxLines = 1,
          modifier = Modifier.weight(1f).basicMarquee(),
        )
        Text(
          stringResource(MR.strings.subjects_credit_formatted, subject.subjectCredit),
          maxLines = 1,
        )
      }
      Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)),
      ) {
        if (subject.subjectExamCoefficient > 0.0) {
          Box(
            Modifier
              .defaultMinSize(0.dp, 16.dp)
              .weight(subject.subjectExamCoefficient.toFloat())
              .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 4.dp, topEnd = 4.dp))
              .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center,
          ) {
            Text(
              stringResource(
                MR.strings.subjects_exam_formatted,
                subject.subjectExamCoefficient,
              ),
              Modifier.padding(4.dp),
              color = MaterialTheme.colorScheme.onPrimary,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
            )
          }
        }
        if (subject.subjectCCCoefficient > 0.0) {
          Box(
            Modifier
              .defaultMinSize(0.dp, 16.dp)
              .weight(subject.subjectCCCoefficient.toFloat())
              .clip(RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 4.dp, topStart = 4.dp))
              .background(MaterialTheme.colorScheme.tertiary),
            contentAlignment = Alignment.Center,
          ) {
            Text(
              stringResource(
                MR.strings.subjects_cc_formatted,
                subject.subjectCCCoefficient,
              ),
              Modifier.padding(4.dp),
              color = MaterialTheme.colorScheme.onTertiary,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
            )
          }
        }
      }
    }
  }
}
