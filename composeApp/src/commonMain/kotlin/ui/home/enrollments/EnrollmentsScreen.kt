package ui.home.enrollments

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import dev.icerock.moko.resources.compose.stringResource
import mehiz.abdallah.progres.domain.models.StudentCardModel
import mehiz.abdallah.progres.i18n.MR
import org.kodein.di.compose.localDI
import org.kodein.di.instance

object EnrollmentsScreen : Screen {

  override val key = uniqueScreenKey

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val viewModel by localDI().instance<EnrollmentsScreenViewModel>()
    val enrollments by viewModel.enrollments.collectAsState()
    Scaffold(
      topBar = {
        TopAppBar(
          title = {
            Text(text = stringResource(MR.strings.home_enrollments))
          },
          navigationIcon = {
            IconButton(onClick = { navigator.pop() }) {
              Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
            }
          }
        )
      },
    ) { paddingValues ->
      LazyColumn(
        Modifier
          .fillMaxSize()
          .padding(paddingValues)
          .padding(horizontal = 16.dp),
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
  }

  @Composable
  fun EnrollmentCard(
    enrollment: StudentCardModel,
    modifier: Modifier = Modifier,
  ) {
    Card(
      colors = CardDefaults.cardColors(
        contentColor = MaterialTheme.colorScheme.surfaceVariant,
      ),
      modifier = modifier
        .fillMaxWidth(),
    ) {
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
        Column(
          modifier = Modifier
            .padding(8.dp),
        ) {
          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            AsyncImage(
              enrollment.establishmentLogo,
              null,
              modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp)),
            )
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
              ) {
                EnrollmentsCardTitleText(stringResource(MR.strings.enrollments_academic_year))
                Text(
                  enrollment.academicYearString,
                  color = MaterialTheme
                    .colorScheme.secondary,
                  fontWeight = FontWeight.ExtraBold
                )
              }
              HorizontalDivider()
              EnrollmentsCardTitleText(stringResource(MR.strings.enrollments_university))
              EnrollmentsCardText(enrollment.establishmentStringLatin)
            }
          }
          HorizontalDivider()
          Row(
            modifier = Modifier
              .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Column {
              EnrollmentsCardTitleText(stringResource(MR.strings.enrollments_cycle))
              EnrollmentsCardText(enrollment.cycleStringLatin)
            }
            Column(
              horizontalAlignment = Alignment.End
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
          HorizontalDivider()
          Column {
            EnrollmentsCardTitleText(stringResource(MR.strings.enrollments_major))
            EnrollmentsCardText(enrollment.ofFieldStringLatin)
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
      fontWeight = FontWeight.Bold
    )
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Composable
  private fun EnrollmentsCardText(
    text: String
  ) {
    Text(
      text,
      modifier = Modifier.basicMarquee(),
      maxLines = 1
    )
  }
}
