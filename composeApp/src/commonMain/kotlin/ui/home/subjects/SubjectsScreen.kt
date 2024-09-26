package ui.home.subjects

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import mehiz.abdallah.progres.domain.models.SubjectModel
import mehiz.abdallah.progres.i18n.MR
import org.koin.compose.viewmodel.koinViewModel

object SubjectsScreen : Screen {
  override val key = uniqueScreenKey

  @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val scope = rememberCoroutineScope()
    val viewModel = koinViewModel<SubjectsScreenViewModel>()
    val subjects by viewModel.subjects.collectAsState()
    Scaffold(
      topBar = {
        TopAppBar(
          title = {
            Text(stringResource(MR.strings.home_subjects))
          },
          navigationIcon = {
            IconButton(onClick = { navigator.pop() }) {
              Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
            }
          },
        )
      },
    ) { paddingValues ->
      if (subjects.isEmpty()) return@Scaffold
      val yearPagerState = rememberPagerState { subjects.keys.size }
      Column(
        modifier = Modifier
          .padding(paddingValues)
      ) {
        PrimaryScrollableTabRow(
          yearPagerState.currentPage,
          divider = {},
        ) {
          subjects.keys.forEachIndexed { index, year ->
            Tab(
              year == subjects.keys.elementAt(yearPagerState.currentPage),
              onClick = { scope.launch { yearPagerState.animateScrollToPage(index) } },
              text = { Text(year) },
            )
          }
        }
        HorizontalDivider()
        HorizontalPager(
          yearPagerState,
        ) { yearPage ->
          val yearSubjects by remember {
            mutableStateOf(
              subjects[
                subjects.keys.elementAt(
                  yearPage,
                ),
              ]!!,
            )
          }
          val semesterPagerState = rememberPagerState { yearSubjects.size }
          Column {
            SecondaryTabRow(semesterPagerState.currentPage) {
              yearSubjects.keys.forEachIndexed { index, semester ->
                Tab(
                  semester == yearSubjects.keys.elementAt(index),
                  onClick = {
                    scope.launch {
                      semesterPagerState.animateScrollToPage(
                        index,
                      )
                    }
                  },
                  text = { Text(semester) },
                )
              }
            }
            HorizontalPager(
              semesterPagerState,
              userScrollEnabled = false,
            ) { currentPage ->
              val subjects by remember {
                mutableStateOf(
                  yearSubjects[
                    yearSubjects.keys.elementAt(
                      currentPage,
                    ),
                  ],
                )
              }
              LazyColumn(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                item { Spacer(Modifier.height(8.dp)) }
                items(subjects!!) { SubjectPercentage(it) }
                item { Spacer(Modifier.height(8.dp)) }
              }
            }
          }
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
      modifier = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        .padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
          .fillMaxWidth(),
      ) {
        Text(
          text = subject.subjectStringLatin,
          maxLines = 1,
          modifier = Modifier
            .weight(1f)
            .basicMarquee(),
        )
        Text(
          stringResource(MR.strings.subjects_credit_formatted, subject.subjectCredit),
          maxLines = 1,
        )
      }
      Row(
        Modifier
          .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
      ) {
        if (subject.subjectExamCoefficient > 0.0) {
          Box(
            Modifier
              .defaultMinSize(0.dp, 16.dp)
              .weight(subject.subjectExamCoefficient.toFloat())
              .clip(
                if (subject.subjectCCCoefficient > 0.0) {
                  RoundedCornerShape(16.dp, 4.dp, 4.dp, 16.dp)
                } else {
                  RoundedCornerShape(50)
                },
              )
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
              .clip(
                if (subject.subjectExamCoefficient > 0.0) {
                  RoundedCornerShape(4.dp, 16.dp, 16.dp, 4.dp)
                } else {
                  RoundedCornerShape(50)
                },
              )
              .background(MaterialTheme.colorScheme.tertiary),
            contentAlignment = Alignment.Center,
          ) {
            Text(
              stringResource(
                MR.strings.subjects_cc_formatted,
                subject.subjectCCCoefficient,
              ),
              Modifier
                .padding(4.dp),
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
