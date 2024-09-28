import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.koin.compose.koinInject
import preferences.BasePreferences
import presentation.theme.AppTheme
import ui.home.HomeScreen
import ui.onboarding.LoginScreen

@Composable
fun App(onReady: () -> Unit) {
  val preferences = koinInject<BasePreferences>()
  AppTheme {
    Navigator(screen = if (preferences.isLoggedIn.get()) HomeScreen else LoginScreen) {
      onReady()
      SlideTransition(it)
    }
  }
}
