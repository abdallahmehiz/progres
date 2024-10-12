package ui.home.discharge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import com.dokar.sonner.ToasterState
import dev.icerock.moko.resources.compose.stringResource
import dev.materii.pullrefresh.PullRefreshLayout
import dev.materii.pullrefresh.rememberPullRefreshState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import mehiz.abdallah.progres.domain.models.DischargeModel
import mehiz.abdallah.progres.i18n.MR
import org.koin.compose.koinInject
import presentation.ErrorScreenContent
import presentation.MaterialPullRefreshIndicator
import presentation.NoDataScreen
import presentation.errorToast
import utils.FirebaseUtils
import utils.isNetworkError

object DischargeScreen : Screen {
  override val key = uniqueScreenKey

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val screenModel = koinScreenModel<DischargeScreenModel>()
    val discharge by screenModel.discharge.collectAsState()
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
            Text(stringResource(MR.strings.home_discharge))
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
        discharge.DisplayResult(
          onLoading = { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) },
          onSuccess = { if (it != null) DischargeScreenContent(it) else NoDataScreen() },
          onError = { ErrorScreenContent(it) },
        )
      }
    }
  }

  @Composable
  fun DischargeScreenContent(
    dischargeState: DischargeModel,
    modifier: Modifier = Modifier,
  ) {
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      if (dischargeState == null) return
      Box(
        Modifier
          .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 4.dp, bottomStart = 4.dp))
          .background(MaterialTheme.colorScheme.tertiaryContainer)
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
      ) {
        Text(
          stringResource(MR.strings.discharge_status),
          color = MaterialTheme.colorScheme.onTertiaryContainer,
          style = MaterialTheme.typography.headlineSmall
        )
      }
      Column(
        modifier = Modifier
          .clip(RoundedCornerShape(topEnd = 4.dp, topStart = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
          .background(MaterialTheme.colorScheme.surfaceContainerHigh)
          .fillMaxWidth()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        DischargeState(stringResource(MR.strings.discharge_department), dischargeState.departmentLevel)
        HorizontalDivider()
        DischargeState(stringResource(MR.strings.discharge_faculty), dischargeState.facultyLevel)
        HorizontalDivider()
        DischargeState(stringResource(MR.strings.discharge_library), dischargeState.centralLibraryLevel)
        HorizontalDivider()
        DischargeState(stringResource(MR.strings.discharge_scholarship), dischargeState.scholarshipLevel)
        HorizontalDivider()
        DischargeState(stringResource(MR.strings.discharge_residence), dischargeState.residenceLevel)
      }
    }
  }

  @Composable
  fun DischargeState(
    title: String,
    state: Boolean,
    modifier: Modifier = Modifier
  ) {
    Row(
      modifier,
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        if (state) Icons.Rounded.Check else Icons.Rounded.Close,
        null,
        tint = if (state) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(
            if (state) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
          )
      )
      Text(
        title,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        color = if (state) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
      )
    }
  }
}
