package ui.home.groups

import androidx.compose.foundation.background
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import mehiz.abdallah.progres.domain.models.GroupModel
import mehiz.abdallah.progres.i18n.MR
import org.koin.compose.koinInject
import presentation.ErrorScreenContent
import presentation.MaterialPullRefreshIndicator
import presentation.NoDataScreen
import presentation.errorToast
import ui.home.examsschedule.abbreviatedDayOfWeekStringResources
import ui.home.examsschedule.abbreviatedMonthStringResources
import utils.FirebaseUtils
import utils.isNetworkError

object GroupsScreen : Screen {
  override val key = uniqueScreenKey

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val screenModel = koinScreenModel<GroupsScreenModel>()
    val groups by screenModel.groups.collectAsState()
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
          title = { Text(stringResource(MR.strings.home_group)) },
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
        ptrState,
        modifier = Modifier
          .padding(paddingValues),
        indicator = { MaterialPullRefreshIndicator(ptrState) }
      ) {
        groups.DisplayResult(
          onLoading = { LinearProgressIndicator(Modifier.fillMaxWidth()) },
          onSuccess = { if (it.isNotEmpty()) GroupsScreenContent(it) else NoDataScreen() },
          onError = { ErrorScreenContent(it) },
        )
      }
    }
  }

  @Composable
  fun GroupsScreenContent(
    groups: ImmutableList<GroupModel>,
    modifier: Modifier = Modifier,
  ) {
    JetLimeColumn(
      ItemsList(groups),
      style = JetLimeDefaults.columnStyle(contentDistance = 16.dp, itemSpacing = 16.dp),
      modifier = modifier.padding(horizontal = 16.dp),
    ) { index, item, position ->
      JetLimeEvent(
        style = JetLimeEventDefaults.eventStyle(
          position = position,
          pointType = if (index == 0) EventPointType.Default else EventPointType.EMPTY,
        ),
      ) {
        Column(
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
            Text(stringResource(MR.strings.groups_assigned_on))
            Text(
              stringResource(
                MR.strings.formatted_date,
                stringResource(abbreviatedDayOfWeekStringResources[item.assignmentDate.dayOfWeek]!!),
                item.assignmentDate.dayOfMonth,
                stringResource(abbreviatedMonthStringResources[item.assignmentDate.month]!!),
                item.assignmentDate.year,
              ),
            )
          }
          Column(
            Modifier
              .clip(RoundedCornerShape(4.dp, 4.dp, 16.dp, 16.dp))
              .background(MaterialTheme.colorScheme.surfaceContainerHigh)
              .padding(horizontal = 16.dp, vertical = 8.dp),
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Text(stringResource(MR.strings.groups_period))
              Text(item.periodStringLatin)
            }
            Row(
              modifier = Modifier
                .fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Text(stringResource(MR.strings.groups_section))
              Text(item.sectionName)
            }
            Row(
              modifier = Modifier
                .fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Text(stringResource(MR.strings.groups_group))
              Text(item.groupName)
            }
          }
        }
      }
      if (item == groups.last()) {
        Spacer(Modifier.height(16.dp))
      }
    }
  }
}
