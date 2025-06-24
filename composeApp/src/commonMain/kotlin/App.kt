import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.dokar.sonner.Toaster
import com.dokar.sonner.rememberToasterState
import com.svenjacobs.reveal.RevealCanvas
import com.svenjacobs.reveal.rememberRevealCanvasState
import dev.icerock.moko.resources.compose.stringResource
import dev.jordond.connectivity.Connectivity
import dev.jordond.connectivity.HttpConnectivityOptions
import dev.jordond.connectivity.compose.rememberConnectivityState
import mehiz.abdallah.progres.i18n.MR
import org.koin.compose.koinInject
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import preferences.BasePreferences
import presentation.AppStateBanners
import presentation.theme.AppTheme
import ui.home.HomeScreen
import ui.onboarding.LoginScreen

@Composable
fun App(onReady: () -> Unit, modifier: Modifier = Modifier) {
  val preferences = koinInject<BasePreferences>()
  val revealCanvasState = rememberRevealCanvasState()
  val toasterState = rememberToasterState()
  val appUpdateChecker = koinInject<AppUpdateCheck>()
  val isThereAnUpdate by appUpdateChecker.isThereAnUpdate.collectAsState()
  val state = rememberConnectivityState().apply { startMonitoring() }
  val httpConnectivity = Connectivity(
    options = HttpConnectivityOptions(
      urls = listOf(stringResource(MR.strings.progres_api_url)),
      pollingIntervalMs = 10 * 1000,
      timeoutMs = 10 * 1000,
    ),
  )
  httpConnectivity.start()
  val httpStatus by httpConnectivity.statusUpdates.collectAsState(Connectivity.Status.Connected(false))
  LaunchedEffect(state.isConnected) {
    httpConnectivity.stop()
    httpConnectivity.start()
  }
  loadKoinModules(
    module {
      single { revealCanvasState }
      single { toasterState }
    },
  )
  var isThereABannerVisible by remember { mutableStateOf(false) }
  LaunchedEffect(isThereAnUpdate, state.isConnected, httpStatus, isThereABannerVisible) {
    isThereABannerVisible = isThereAnUpdate || !state.isConnected || httpStatus !is Connectivity.Status.Connected
  }
  AppTheme {
    RevealCanvas(revealCanvasState) {
      Column(modifier) {
        AppStateBanners(
          newUpdate = isThereAnUpdate,
          noInternet = !state.isConnected && state.isMonitoring,
          cantReach = httpStatus !is Connectivity.Status.Connected && httpConnectivity.isMonitoring,
          modifier = Modifier.fillMaxWidth(),
        )
        Navigator(screen = if (preferences.isLoggedIn.get()) HomeScreen else LoginScreen) {
          onReady()
          SlideTransition(
            it,
            modifier = if (isThereABannerVisible) {
              Modifier.consumeWindowInsets(WindowInsets.statusBars)
            } else {
              Modifier
            },
          )
        }
      }
      Toaster(toasterState, richColors = true)
    }
  }
}
