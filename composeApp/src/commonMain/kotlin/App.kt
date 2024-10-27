import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import com.svenjacobs.reveal.RevealCanvas
import com.svenjacobs.reveal.rememberRevealCanvasState
import org.koin.compose.koinInject
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import preferences.BasePreferences
import presentation.SlideScreenTransition
import presentation.theme.AppTheme
import ui.home.HomeScreen
import ui.onboarding.LoginScreen

@Composable
fun App(onReady: () -> Unit, modifier: Modifier = Modifier) {
  val preferences = koinInject<BasePreferences>()
  val revealCanvasState = rememberRevealCanvasState()
  loadKoinModules(
    module {
      single { revealCanvasState }
    },
  )
  AppTheme {
    RevealCanvas(revealCanvasState, modifier) {
      Navigator(screen = if (preferences.isLoggedIn.get()) HomeScreen else LoginScreen) {
        onReady()
        SlideScreenTransition(it)
      }
    }
  }
}
