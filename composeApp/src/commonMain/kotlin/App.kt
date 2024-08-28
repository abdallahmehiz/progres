import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.kodein.di.compose.localDI
import org.kodein.di.instance
import preferences.BasePreferences
import presentation.theme.AppTheme
import ui.home.HomeScreen
import ui.onboarding.LoginScreen

@Composable
fun App() {
  val preferences by localDI().instance<BasePreferences>()
  AppTheme {
    Navigator(screen = if (preferences.isLoggedIn.get().also(::println)) HomeScreen else LoginScreen) {
      SlideTransition(it)
    }
  }
}
