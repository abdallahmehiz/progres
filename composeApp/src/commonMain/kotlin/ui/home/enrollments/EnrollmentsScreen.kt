package ui.home.enrollments

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.dokar.sonner.ToasterState
import dev.icerock.moko.resources.compose.stringResource
import dev.materii.pullrefresh.PullRefreshLayout
import dev.materii.pullrefresh.rememberPullRefreshState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import mehiz.abdallah.progres.domain.models.StudentCardModel
import mehiz.abdallah.progres.i18n.MR
import org.koin.compose.koinInject
import presentation.ErrorScreenContent
import presentation.MaterialPullRefreshIndicator
import presentation.NoDataScreen
import presentation.errorToast
import utils.FirebaseUtils
import utils.isNetworkError

object EnrollmentsScreen : Screen {

  override val key = uniqueScreenKey

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val screenModel = koinScreenModel<EnrollmentsScreenModel>()
    val enrollments by screenModel.enrollments.collectAsState()
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
          title = { Text(text = stringResource(MR.strings.home_enrollments)) },
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
        enrollments.DisplayResult(
          onLoading = { LinearProgressIndicator(Modifier.fillMaxWidth()) },
          onSuccess = { if (it.isNotEmpty()) EnrollmentsScreenContent(it) else NoDataScreen() },
          onError = { ErrorScreenContent(it) },
        )
      }
    }
  }

  @Composable
  fun EnrollmentsScreenContent(
    enrollments: ImmutableList<StudentCardModel>,
    modifier: Modifier = Modifier,
  ) {
    LazyColumn(
      modifier.fillMaxSize().padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      items(enrollments) {
        EnrollmentCard(it)
      }
      item {
        Spacer(Modifier.height(16.dp))
      }
    }
  }

  @Composable
  fun EnrollmentCard(
    enrollment: StudentCardModel,
    modifier: Modifier = Modifier,
  ) {
    Card(
      colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHigh),
      modifier = modifier.fillMaxWidth(),
    ) {
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
        Column(
          modifier = Modifier.padding(8.dp),
        ) {
          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            AsyncImage(
              enrollment.establishment.photo,
              null,
              modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)),
            )
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
              ) {
                EnrollmentsCardTitleText(stringResource(MR.strings.enrollments_academic_year))
                Text(
                  enrollment.academicYearString,
                  color = MaterialTheme.colorScheme.secondary,
                  fontWeight = FontWeight.ExtraBold,
                )
              }
              HorizontalDivider()
              EnrollmentsCardTitleText(stringResource(MR.strings.enrollments_university))
              EnrollmentsCardText(enrollment.establishment.nameLatin)
            }
          }
          HorizontalDivider()
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Column {
              EnrollmentsCardTitleText(stringResource(MR.strings.enrollments_cycle))
              EnrollmentsCardText(enrollment.cycleStringLatin)
            }
            Column(
              horizontalAlignment = Alignment.End,
            ) {
              EnrollmentsCardTitleText(stringResource(MR.strings.enrollments_level))
              EnrollmentsCardText(enrollment.levelStringLongLatin)
            }
          }
          HorizontalDivider()
          Column {
            EnrollmentsCardTitleText(stringResource(MR.strings.enrollments_field))
            EnrollmentsCardText(enrollment.ofDomainStringLatin)
          }
          if (enrollment.ofFieldStringLatin != null) {
            HorizontalDivider()
            Column {
              EnrollmentsCardTitleText(stringResource(MR.strings.enrollments_major))
              EnrollmentsCardText(enrollment.ofFieldStringLatin!!)
            }
          }
          if (enrollment.ofSpecialtyStringLatin != null) {
            HorizontalDivider()
            Column {
              EnrollmentsCardTitleText(stringResource(MR.strings.enrollments_specialty))
              EnrollmentsCardText(enrollment.ofSpecialtyStringLatin!!)
            }
          }
        }
      }
    }
  }

  @Composable
  private fun EnrollmentsCardTitleText(
    text: String,
  ) {
    Text(
      text,
      color = MaterialTheme.colorScheme.tertiary.copy(.5f),
      fontWeight = FontWeight.Bold,
    )
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Composable
  private fun EnrollmentsCardText(
    text: String,
  ) {
    Text(
      text,
      modifier = Modifier.basicMarquee(),
      maxLines = 1,
    )
  }
}
