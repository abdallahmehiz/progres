import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.LayoutDirection
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.koin.compose.koinInject
import preferences.BasePreferences
import preferences.Language
import preferences.preference.Preference
import preferences.preference.collectAsState
import presentation.theme.AppTheme
import ui.home.HomeScreen
import ui.onboarding.LoginScreen

@Composable
fun App(onReady: () -> Unit) {
  val preferences = koinInject<BasePreferences>()
  CompositionLocalProvider(LocalLayoutDirection provides getLayoutDirection(preferences.language)) {
    AppTheme {
      Navigator(screen = if (preferences.isLoggedIn.get()) HomeScreen else LoginScreen) {
        onReady()
        SlideTransition(it)
      }
    }
  }
}

val getLayoutDirection: @Composable (Preference<Language>) -> LayoutDirection = {
  val language by it.collectAsState()
  if (language == Language.Arabic ||
    (language == Language.System && Locale.current.language.startsWith("ar"))
  ) {
    LayoutDirection.Rtl
  } else {
    LayoutDirection.Ltr
  }
}
