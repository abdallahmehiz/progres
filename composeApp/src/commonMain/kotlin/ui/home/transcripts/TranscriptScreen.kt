package ui.home.transcripts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
import mehiz.abdallah.progres.domain.models.AcademicDecisionModel
import mehiz.abdallah.progres.domain.models.TranscriptModel
import mehiz.abdallah.progres.domain.models.TranscriptSubjectModel
import mehiz.abdallah.progres.domain.models.TranscriptUeModel
import mehiz.abdallah.progres.i18n.MR
import org.koin.compose.koinInject
import presentation.ErrorScreenContent
import presentation.MaterialPullRefreshIndicator
import presentation.NoDataScreen
import presentation.errorToast
import ui.home.ccgrades.PeriodPlusAcademicYearText
import utils.FirebaseUtils
import utils.isNetworkError

object TranscriptScreen : Screen {

  override val key = uniqueScreenKey

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val screenModel = koinScreenModel<TranscriptsScreenModel>()
    val transcripts by screenModel.transcripts.collectAsState()
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
          title = {
            Text(stringResource(MR.strings.home_academic_transcripts))
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
        ptrState,
        modifier = Modifier.padding(paddingValues),
        indicator = { MaterialPullRefreshIndicator(ptrState) },
      ) {
        transcripts.DisplayResult(
          onLoading = { LinearProgressIndicator(Modifier.fillMaxWidth()) },
          onSuccess = { if (it.isNotEmpty()) TranscriptScreenContent(it) else NoDataScreen() },
          onError = { ErrorScreenContent(it) },
        )
      }
    }
  }

  @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
  @Composable
  fun TranscriptScreenContent(
    transcripts: ImmutableMap<String, Pair<AcademicDecisionModel?, List<TranscriptModel>>>,
    modifier: Modifier = Modifier,
  ) {
    val scope = rememberCoroutineScope()
    Column(modifier) {
      val pagerState = rememberPagerState(transcripts.keys.size - 1) { transcripts.keys.size }
      PrimaryScrollableTabRow(
        pagerState.currentPage,
        divider = {},
        modifier = Modifier.fillMaxWidth(),
      ) {
        transcripts.keys.forEachIndexed { index, year ->
          Tab(
            index == pagerState.currentPage,
            text = {
              PeriodPlusAcademicYearText(year, transcripts[year]!!.second.first().period.academicYearStringLatin)
            },
            onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
          )
        }
      }
      HorizontalDivider()
      HorizontalPager(pagerState) { currentPage ->
        val currentPeriod = remember { transcripts.keys.elementAt(currentPage) }
        val currentPeriodDecision = remember { transcripts[currentPeriod]!!.first }
        val currentPeriodTranscripts = remember { transcripts[currentPeriod]!!.second }
        Column(
          modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          Spacer(Modifier)
          if (currentPeriodDecision != null) {
            AcademicDecisionCard(currentPeriodDecision)
          }
          currentPeriodTranscripts.forEach {
            ReportCard(transcript = it)
          }
          Spacer(Modifier.height(8.dp))
        }
      }
    }
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Composable
  fun ReportCard(
    transcript: TranscriptModel,
    modifier: Modifier = Modifier,
  ) {
    var expandedState by remember { mutableStateOf(true) }
    val rotationState by animateFloatAsState(
      targetValue = if (expandedState) 180f else 0f,
      label = "arrow_rotation",
    )
    Card(
      modifier = modifier.fillMaxWidth().animateContentSize(
        animationSpec = tween(
          durationMillis = 300,
          easing = LinearOutSlowInEasing,
        ),
      ),
      shape = RoundedCornerShape(16.dp),
      onClick = { expandedState = !expandedState },
    ) {
      Row(
        modifier = Modifier.padding(start = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          modifier = Modifier.weight(6f).basicMarquee(),
          text = transcript.period.periodStringLatin,
          maxLines = 1,
        )
        Box {
          Text(
            modifier = Modifier,
            text = stringResource(MR.strings.grade_int, transcript.average ?: 0f, 20),
            color = if ((transcript.average ?: 0.0) < 10) {
              MaterialTheme.colorScheme.error
            } else {
              MaterialTheme.colorScheme.primary
            },
            fontWeight = FontWeight.Bold
          )
        }
        IconButton(
          modifier = Modifier.weight(1f).alpha(0.4f).rotate(rotationState),
          onClick = { expandedState = !expandedState },
        ) { Icon(Icons.Rounded.ArrowDropDown, null) }
      }
      AnimatedVisibility(
        expandedState,
        enter = expandVertically { -it },
        exit = shrinkVertically { -it }
      ) {
        ReportCardContent(transcript)
      }
      Spacer(Modifier.height(8.dp))
    }
  }

  @Composable
  fun ReportCardContent(
    transcript: TranscriptModel,
    modifier: Modifier = Modifier,
  ) {
    Column(
      modifier = modifier.fillMaxSize(),
    ) {
      ReportCardHeader()
      Column {
        transcript.ues.forEach { ue ->
          UE(ue)
        }
      }
    }
  }

  @Composable
  fun ReportCardHeader(
    modifier: Modifier = Modifier,
  ) {
    Row(
      modifier = modifier.fillMaxWidth().padding(start = 8.dp),
      horizontalArrangement = Arrangement.Absolute.SpaceBetween,
    ) {
      Text(
        stringResource(MR.strings.transcripts_subject),
        modifier = Modifier.weight(3f),
        style = MaterialTheme.typography.labelSmall,
      )
      Text(
        stringResource(MR.strings.transcripts_credit),
        modifier = Modifier.weight(1.2f),
        style = MaterialTheme.typography.labelSmall,
      )
      Text(
        stringResource(MR.strings.transcripts_coef),
        modifier = Modifier.weight(.8f),
        style = MaterialTheme.typography.labelSmall,
      )
      Text(
        stringResource(MR.strings.transcripts_average),
        modifier = Modifier.weight(1f),
        style = MaterialTheme.typography.labelSmall,
      )
    }
  }

  @Composable
  fun UE(
    ue: TranscriptUeModel,
    modifier: Modifier = Modifier,
  ) {
    Column(modifier = modifier.fillMaxWidth()) {
      HorizontalDivider()
      Row(
        modifier = Modifier.fillMaxWidth().background(
          if (ue.average < 10) {
            MaterialTheme.colorScheme.error.copy(0.2f)
          } else {
            MaterialTheme.colorScheme.primary.copy(0.2f)
          },
        ).padding(start = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          stringResource(MR.strings.transcripts_ue_code_formatted, ue.ueNatureCode, ue.ueCode),
          style = MaterialTheme.typography.labelSmall,
          modifier = Modifier.weight(3f),
        )
        Text(
          stringResource(MR.strings.grade, ue.creditObtained, ue.credit),
          style = MaterialTheme.typography.labelSmall,
          modifier = Modifier.weight(1.2f),
        )
        Text(
          stringResource(MR.strings.generic_float, ue.coefficient),
          style = MaterialTheme.typography.labelSmall,
          modifier = Modifier.weight(.8f),
        )
        Text(
          stringResource(MR.strings.grade_int, ue.average, 20),
          style = MaterialTheme.typography.labelSmall,
          modifier = Modifier.weight(1f),
          color = if (ue.average < 10) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        )
      }
      ue.subjects.forEach { subject ->
        HorizontalDivider()
        Subject(subject = subject)
      }
    }
  }

  @Composable
  fun Subject(
    subject: TranscriptSubjectModel,
    modifier: Modifier = Modifier,
  ) {
    Row(
      modifier = modifier.fillMaxWidth().padding(start = 8.dp),
      horizontalArrangement = Arrangement.End,
    ) {
      Text(
        subject.subjectStringLatin,
        style = MaterialTheme.typography.labelSmall,
        maxLines = 1,
        modifier = Modifier.weight(3f),
      )
      Text(
        stringResource(MR.strings.generic_float, subject.credit),
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier.weight(1.2f),
      )
      Text(
        stringResource(MR.strings.generic_float, subject.coefficient),
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier.weight(.8f),
      )
      Text(
        stringResource(MR.strings.grade_int, subject.average ?: 0f, 20),
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier.weight(1f),
        color = if ((subject.average ?: 0.0) < 10) {
          MaterialTheme.colorScheme.error
        } else {
          MaterialTheme.colorScheme.primary
        },
      )
    }
  }

  @Composable
  fun AcademicDecisionCard(
    model: AcademicDecisionModel,
    modifier: Modifier = Modifier,
  ) {
    if (model.decisionStringLatin == null || model.average == null || model.creditAcquired == null) return
    Card(modifier = modifier.fillMaxWidth()) {
      Column(
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Text(
          text = stringResource(MR.strings.transcripts_decision, model.decisionStringLatin!!),
          color = if (model.average!! < 10) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
          style = MaterialTheme.typography.bodyMedium,
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Box(
            modifier = Modifier.clip(RoundedCornerShape(50f)).background(
              if (model.average!! < 10) {
                MaterialTheme.colorScheme.error
              } else {
                MaterialTheme.colorScheme.primary
              },
            ),
          ) {
            Text(
              text = stringResource(
                MR.strings.transcripts_decision_average,
                stringResource(MR.strings.grade_int, model.average!!, 20),
              ),
              color = if (model.average!! < 10) {
                MaterialTheme.colorScheme.onError
              } else {
                MaterialTheme.colorScheme.onPrimary
              },
              style = MaterialTheme.typography.bodyMedium,
              modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
            )
          }
          Box(
            modifier = Modifier.clip(RoundedCornerShape(50f)).background(MaterialTheme.colorScheme.tertiaryContainer),
          ) {
            Text(
              text = stringResource(
                MR.strings.transcripts_decision_credit,
                model.creditAcquired!!
              ),
              color = MaterialTheme.colorScheme.onTertiaryContainer,
              style = MaterialTheme.typography.bodyMedium,
              modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
            )
          }
        }
      }
    }
  }
}
