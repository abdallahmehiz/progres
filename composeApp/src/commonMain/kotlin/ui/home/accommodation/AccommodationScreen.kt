package ui.home.accommodation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dokar.sonner.ToasterState
import com.pushpal.jetlime.EventPointType
import com.pushpal.jetlime.ItemsList
import com.pushpal.jetlime.JetLimeColumn
import com.pushpal.jetlime.JetLimeDefaults
import com.pushpal.jetlime.JetLimeEvent
import com.pushpal.jetlime.JetLimeEventDefaults
import dev.icerock.moko.resources.compose.stringResource
import dev.materii.pullrefresh.PullRefreshLayout
import dev.materii.pullrefresh.rememberPullRefreshState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import mehiz.abdallah.progres.domain.models.AccommodationModel
import mehiz.abdallah.progres.i18n.MR
import org.koin.compose.koinInject
import presentation.ErrorScreenContent
import presentation.MaterialPullRefreshIndicator
import presentation.NoDataScreen
import presentation.errorToast
import utils.FirebaseUtils
import utils.isNetworkError

object AccommodationScreen : Screen {

  override val key = uniqueScreenKey

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val screenModel = koinScreenModel<AccommodationScreenModel>()
    val accommodation by screenModel.accommodations.collectAsState()
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
            Text(stringResource(MR.strings.home_accommodation))
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
        indicator = { MaterialPullRefreshIndicator(ptrState) },
        modifier = Modifier.padding(paddingValues),
      ) {
        accommodation.DisplayResult(
          onLoading = { LinearProgressIndicator(Modifier.fillMaxWidth()) },
          onSuccess = { if (it.isNotEmpty()) AccommodationScreenContent(it) else NoDataScreen() },
          onError = { ErrorScreenContent(it) },
        )
      }
    }
  }

  @Composable
  fun AccommodationScreenContent(
    accommodations: ImmutableList<AccommodationModel>,
    modifier: Modifier = Modifier,
  ) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
      JetLimeColumn(
        ItemsList(accommodations),
        style = JetLimeDefaults.columnStyle(contentDistance = 16.dp, itemSpacing = 16.dp),
        modifier = modifier.padding(horizontal = 16.dp),
      ) { index, item, position ->
        JetLimeEvent(
          style = JetLimeEventDefaults.eventStyle(
            position = position,
            pointType = if (index == 0) EventPointType.Default else EventPointType.EMPTY,
          ),
        ) { AccommodationCard(item) }
        if (item == accommodations.last()) {
          Spacer(Modifier.height(16.dp))
        }
      }
    }
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Composable
  fun AccommodationCard(
    accommodation: AccommodationModel,
    modifier: Modifier = Modifier,
  ) {
    Column(
      modifier = modifier,
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Row(
        modifier = Modifier
          .clip(RoundedCornerShape(16.dp, 16.dp, 4.dp, 4.dp))
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.tertiaryContainer)
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          stringResource(MR.strings.accommodation_year),
          fontWeight = FontWeight.ExtraBold,
        )
        Text(
          accommodation.academicYearString,
          fontWeight = FontWeight.ExtraBold,
        )
      }
      Column(
        Modifier
          .clip(RoundedCornerShape(4.dp, 4.dp, 16.dp, 16.dp))
          .background(MaterialTheme.colorScheme.surfaceContainerHigh)
          .padding(horizontal = 16.dp, vertical = 8.dp),
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Text(
            stringResource(MR.strings.accommodation_provider),
            fontWeight = FontWeight.ExtraBold,
          )
          Text(
            accommodation.providerStringLatin,
            maxLines = 1,
            modifier = Modifier.basicMarquee(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
        Column(
          modifier = Modifier
            .fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Text(
            stringResource(MR.strings.accommodation_residence),
            fontWeight = FontWeight.ExtraBold,
          )
          Text(
            accommodation.residenceStringLatin,
            maxLines = 1,
            modifier = Modifier.basicMarquee(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
        if (accommodation.assignedPavillion != null) {
          Column(
            modifier = Modifier
              .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            Text(
              stringResource(MR.strings.accommodation_pavilion),
              fontWeight = FontWeight.ExtraBold,
            )
            Text(
              accommodation.assignedPavillion!!,
              maxLines = 1,
              modifier = Modifier.basicMarquee(),
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }
      }
    }
  }
}
