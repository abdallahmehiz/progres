package ui.home.ccgradesscreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
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
import mehiz.abdallah.progres.domain.models.CCGradeModel
import mehiz.abdallah.progres.i18n.MR
import org.koin.compose.koinInject
import presentation.ErrorScreenContent
import presentation.MaterialPullRefreshIndicator
import presentation.errorToast

object CCGradesScreen : Screen {

  override val key = uniqueScreenKey

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun Content() {
    val scope = rememberCoroutineScope()
    val navigator = LocalNavigator.currentOrThrow
    val screenModel = koinScreenModel<CCGradesScreenModel>()
    val data by screenModel.ccGrades.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    val toasterState = koinInject<ToasterState>()
    val ptrState = rememberPullRefreshState(
      refreshing = isRefreshing,
      onRefresh = {
        isRefreshing = true
        scope.launch(Dispatchers.IO) {
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
          title = { Text(stringResource(MR.strings.home_continuous_eval)) },
          navigationIcon = {
            IconButton(onClick = navigator::pop) {
              Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
            }
          },
          windowInsets = WindowInsets(0.dp)
        )
      },
    ) { paddingValues ->
      PullRefreshLayout(
        state = ptrState,
        modifier = Modifier.padding(paddingValues),
        indicator = { MaterialPullRefreshIndicator(ptrState) },
      ) {
        data.DisplayResult(
          onLoading = { LinearProgressIndicator(Modifier.fillMaxWidth()) },
          onSuccess = { CCGradesScreenContent(it) },
          onError = { ErrorScreenContent(it) },
        )
      }
    }
  }

  @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
  @Composable
  fun CCGradesScreenContent(
    data: ImmutableMap<AcademicPeriodModel, List<CCGradeModel>>,
    modifier: Modifier = Modifier,
  ) {
    val scope = rememberCoroutineScope()
    Column(
      modifier = modifier,
    ) {
      val pagerState = rememberPagerState(data.keys.size - 1) { data.keys.size }
      PrimaryScrollableTabRow(
        pagerState.currentPage,
        divider = {},
      ) {
        data.keys.forEachIndexed { index, period ->
          Tab(
            selected = index == pagerState.currentPage,
            text = { PeriodPlusAcademicYearText(period.periodStringLatin, period.academicYearStringLatin) },
            onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
          )
        }
      }
      HorizontalDivider()
      HorizontalPager(
        pagerState,
      ) { currentPage ->
        Column {
          Spacer(Modifier.height(16.dp))
          CCGradesCollection(
            data[data.keys.elementAt(currentPage)]!!.toImmutableList(),
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
          )
          Spacer(Modifier.height(16.dp))
        }
      }
    }
  }

  @Composable
  fun CCGradesCollection(
    grades: ImmutableList<CCGradeModel>,
    modifier: Modifier = Modifier,
  ) {
    LazyColumn(
      modifier,
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      items(grades) {
        CCGradeListing(it)
      }
    }
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Composable
  fun CCGradeListing(
    grade: CCGradeModel,
    modifier: Modifier = Modifier,
  ) {
    Row(
      modifier.clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceContainerHigh)
        .padding(end = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(4.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Box(
        modifier = Modifier.size(32.dp).background(
          when (grade.ap) {
            "TP" -> MaterialTheme.colorScheme.tertiaryContainer
            "TD" -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.secondaryContainer
          },
        ),
        contentAlignment = Alignment.Center,
      ) {
        Text(grade.ap)
      }
      Text(
        text = grade.subjectStringLatin,
        maxLines = 1,
        modifier = Modifier.weight(1f).basicMarquee(),
      )
      Text(
        stringResource(MR.strings.grade, grade.grade ?: 0, 20),
        maxLines = 1,
        color = if ((grade.grade ?: 0.0) >= 10) {
          MaterialTheme.colorScheme.primary
        } else {
          MaterialTheme.colorScheme.error
        },
      )
    }
  }
}

@Composable
fun PeriodPlusAcademicYearText(
  period: String,
  academicYear: String,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      period,
      style = MaterialTheme.typography.bodyLarge,
    )
    Text(
      academicYear,
      style = MaterialTheme.typography.bodyMedium,
    )
  }
}
