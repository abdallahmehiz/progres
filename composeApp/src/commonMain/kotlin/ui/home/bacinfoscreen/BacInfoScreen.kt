package ui.home.bacinfoscreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import dev.icerock.moko.resources.compose.stringResource
import mehiz.abdallah.progres.i18n.MR

object BacInfoScreen : Screen {

  override val key = uniqueScreenKey

  @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val screenModel = koinScreenModel<BacInfoScreenModel>()
    val bacInfo by screenModel.bacInfo.collectAsState()
    val studentPhoto by screenModel.studentPhoto.collectAsState()
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text(stringResource(MR.strings.home_bac_results)) },
          navigationIcon = {
            IconButton(onClick = navigator::pop) {
              Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
            }
          },
          windowInsets = WindowInsets(0.dp)
        )
      },
    ) { paddingValues ->
      if (bacInfo == null) return@Scaffold
      Column(
        Modifier
          .padding(paddingValues)
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        StudentHeader(
          "${bacInfo!!.firstNameLatin} ${bacInfo!!.lastNameLatin}",
          photo = studentPhoto,
          series = bacInfo!!.seriesString,
          bacYear = bacInfo!!.bacYear,
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp, bottomEnd = 4.dp, bottomStart = 4.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp),
        )
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text(stringResource(MR.strings.generic_average))
          Text(stringResource(MR.strings.grade, bacInfo!!.grade, 20))
        }
        LazyColumn(
          modifier = Modifier
            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          items(
            bacInfo!!.grades,
          ) {
            Column {
              Row(
                modifier = Modifier
                  .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
              ) {
                Text(
                  it.subjectName,
                  maxLines = 1,
                  modifier = Modifier
                    .basicMarquee(),
                )
                Text(stringResource(MR.strings.grade, it.grade, 20))
              }
              if (it != bacInfo!!.grades.last()) {
                HorizontalDivider(
                  color = MaterialTheme.colorScheme.onSecondaryContainer
                )
              }
            }
          }
        }
      }
    }
  }

  @Composable
  fun StudentHeader(
    studentName: String,
    photo: ByteArray?,
    series: String,
    bacYear: Int,
    modifier: Modifier = Modifier,
  ) {
    Row(
      modifier,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      AsyncImage(
        photo,
        null,
        modifier = Modifier
          .heightIn(max = 120.dp)
          .widthIn(max = 76.dp)
          .clip(RoundedCornerShape(16.dp)),
      )
      Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Text(studentName)
        Text(series)
        Text(stringResource(MR.strings.bac_year_x, bacYear))
      }
    }
  }
}
