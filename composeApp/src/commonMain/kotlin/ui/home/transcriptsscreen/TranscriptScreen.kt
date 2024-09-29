package ui.home.transcriptsscreen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import dev.materii.pullrefresh.DragRefreshLayout
import dev.materii.pullrefresh.rememberPullRefreshState
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.coroutines.launch
import mehiz.abdallah.progres.domain.models.AcademicDecisionModel
import mehiz.abdallah.progres.domain.models.TranscriptModel
import mehiz.abdallah.progres.domain.models.TranscriptSubjectModel
import mehiz.abdallah.progres.domain.models.TranscriptUeModel
import mehiz.abdallah.progres.i18n.MR
import org.koin.compose.viewmodel.koinViewModel

object TranscriptScreen : Screen {

  override val key = uniqueScreenKey

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val viewModel = koinViewModel<TranscriptsScreenViewModel>()
    val transcripts by viewModel.transcripts.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val ptrState = rememberPullRefreshState(isRefreshing, { viewModel.refresh() })
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
        )
      },
    ) { paddingValues ->
      DragRefreshLayout(
        ptrState,
        modifier = Modifier.padding(paddingValues)
      ) {
        transcripts.DisplayResult(
          onLoading = { LinearProgressIndicator(Modifier.fillMaxWidth()) },
          onSuccess = { TranscriptScreenContent(it) },
          onError = {},
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
      val pagerState = rememberPagerState { transcripts.keys.size }
      PrimaryScrollableTabRow(
        pagerState.currentPage,
        divider = {},
        modifier = Modifier.fillMaxWidth(),
      ) {
        transcripts.keys.forEachIndexed { index, year ->
          Tab(
            index == pagerState.currentPage,
            text = { Text(year) },
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
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Spacer(Modifier.height(8.dp))
          if (currentPeriodDecision != null) {
            AcademicDecisionCard(currentPeriodDecision)
          }
          currentPeriodTranscripts.forEach {
            ReportCard(transcript = it)
          }
          Spacer(Modifier.height(16.dp))
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
    var expandedState by remember { mutableStateOf(false) }
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
      onClick = {
        expandedState = !expandedState
      },
    ) {
      Row(
        modifier = Modifier.padding(start = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        if (transcript.period != null) {
          Text(
            modifier = Modifier.weight(6f).basicMarquee(),
            text = transcript.period!!.periodStringLatin,
            maxLines = 1,
          )
        }
        Box {
          Text(
            modifier = Modifier,
            text = stringResource(MR.strings.grade, transcript.average ?: 0f, 20),
            color = if ((transcript.average ?: 0.0) < 10) {
              MaterialTheme.colorScheme.error
            } else {
              MaterialTheme.colorScheme.primary
            },
          )
        }
        IconButton(
          modifier = Modifier.weight(1f).alpha(0.4f).rotate(rotationState),
          onClick = { expandedState = !expandedState },
        ) { Icon(Icons.Default.ArrowDropDown, null) }
      }
      if (expandedState) {
        ReportCardContent(transcript)
        Spacer(Modifier.height(8.dp))
      }
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
        "Subject",
        modifier = Modifier.weight(3f),
        style = MaterialTheme.typography.labelSmall,
      )
      Text(
        "Credit",
        modifier = Modifier.weight(1f),
        style = MaterialTheme.typography.labelSmall,
      )
      Text(
        "Coef.",
        modifier = Modifier.weight(1f),
        style = MaterialTheme.typography.labelSmall,
      )
      Text(
        "Average",
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
          "(${ue.ueNatureCode}) ${ue.ueCode}",
          style = MaterialTheme.typography.labelSmall,
          modifier = Modifier.weight(3f),
        )
        Text(
          stringResource(MR.strings.grade, ue.creditObtained, ue.credit),
          style = MaterialTheme.typography.labelSmall,
          modifier = Modifier.weight(1f),
        )
        Text(
          ue.coefficient.toString(),
          style = MaterialTheme.typography.labelSmall,
          modifier = Modifier.weight(1f),
        )
        Text(
          stringResource(MR.strings.grade, ue.average, 20),
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
        subject.credit.toString(),
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier.weight(1f),
      )
      Text(
        subject.coefficient.toString(),
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier.weight(1f),
      )
      Text(
        stringResource(MR.strings.grade, subject.average, 20),
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier.weight(1f),
        color = if (subject.average < 10) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
      )
    }
  }

  @Composable
  fun AcademicDecisionCard(
    model: AcademicDecisionModel,
    modifier: Modifier = Modifier,
  ) {
    if (model.decisionStringLatin == null || model.average == null || model.creditAcquired == null) return
    Card(
      modifier = modifier.fillMaxWidth(),
    ) {
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
                MR.strings.transcripts_average,
                stringResource(MR.strings.grade, model.average!!, 20),
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
              text = stringResource(MR.strings.transcripts_credit, model.creditAcquired!!),
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
