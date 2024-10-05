import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.LayoutDirection
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
import preferences.Language
import preferences.preference.Preference
import presentation.theme.AppTheme
import ui.home.HomeScreen
import ui.onboarding.LoginScreen

@Composable
fun App(onReady: () -> Unit, modifier: Modifier = Modifier) {
  val preferences = koinInject<BasePreferences>()

  val revealCanvasState = rememberRevealCanvasState()
  val toasterState = rememberToasterState()
  loadKoinModules(
    module {
      single { revealCanvasState }
      single { toasterState }
    },
  )
  CompositionLocalProvider(LocalLayoutDirection provides getLayoutDirection(preferences.language)) {
    AppTheme {
      RevealCanvas(revealCanvasState) {
        Column(modifier) {
          Surface(color = MaterialTheme.colorScheme.surfaceDim) { ConnectivityStatusBar() }
          Navigator(screen = if (preferences.isLoggedIn.get()) HomeScreen else LoginScreen) {
            onReady()
            SlideTransition(it)
          }
        }
        Toaster(toasterState, richColors = true)
      }
    }
  }
}

@Composable
fun ConnectivityStatusBar(
  modifier: Modifier = Modifier,
) {
  val state = rememberConnectivityState().apply { startMonitoring() }
  val httpConnectivity = Connectivity(
    options = HttpConnectivityOptions(
      urls = listOf(stringResource(MR.strings.progres_api_url)),
    ),
  )
  httpConnectivity.start()
  val isHttpMonitoring by httpConnectivity.isMonitoring.collectAsState()
  val httpStatus by httpConnectivity.statusUpdates.collectAsState(Connectivity.Status.Connected(false))
  LaunchedEffect(state.isConnected) {
    httpConnectivity.stop()
    httpConnectivity.start()
  }
  Box(modifier = modifier.windowInsetsPadding(WindowInsets.statusBars))
  if (isHttpMonitoring && state.isMonitoring) {
    Row(
      modifier
        .fillMaxWidth()
        .animateContentSize()
        .background(MaterialTheme.colorScheme.errorContainer),
      horizontalArrangement = Arrangement.Center,
    ) {
      if (state.isDisconnected || httpStatus.isDisconnected) {
        Text(
          stringResource(
            if (state.isDisconnected) MR.strings.connectivity_no_internet else MR.strings.connectivity_no_progres,
          ),
          color = MaterialTheme.colorScheme.onErrorContainer,
          modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
        )
      }
    }
  }
}

val getLayoutDirection: (Preference<Language>) -> LayoutDirection = {
  val language = it.get()
  if ( // language == Language.Arabic ||
    (language == Language.System && Locale.current.language.startsWith("ar"))
  ) {
    LayoutDirection.Rtl
  } else {
    LayoutDirection.Ltr
  }
  LayoutDirection.Ltr // for now, no RTL layout.
}
