package mehiz.abdallah.progres

import App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import org.koin.compose.koinInject
import preferences.BasePreferences
import preferences.preference.collectAsState
import presentation.theme.DarkMode

class MainActivity : ComponentActivity() {
  private var isLoading by mutableStateOf(false)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    installSplashScreen().setKeepOnScreenCondition { isLoading }

    setContent {
      val preferences = koinInject<BasePreferences>()
      val darkMode by preferences.darkMode.collectAsState()
      val isSystemInDarkTheme = isSystemInDarkTheme()
      enableEdgeToEdge(
        SystemBarStyle.auto(
          lightScrim = Color.Transparent.toArgb(),
          darkScrim = Color.Transparent.toArgb()
        ) { darkMode == DarkMode.Dark || (darkMode == DarkMode.System && isSystemInDarkTheme) },
      )
      App({ isLoading = false })
    }
  }
}
