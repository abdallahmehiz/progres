package mehiz.abdallah.progres

import App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.compose.withDI
import org.kodein.di.instance
import preferences.BasePreferences
import preferences.preference.collectAsState
import presentation.theme.DarkMode

class MainActivity : ComponentActivity(), DIAware {
  override val di by closestDI(this)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      val preferences by di.instance<BasePreferences>()
      val darkMode by preferences.darkMode.collectAsState()
      val isSystemInDarkTheme = isSystemInDarkTheme()
      enableEdgeToEdge(
        SystemBarStyle.auto(
          lightScrim = Color.White.toArgb(),
          darkScrim = Color.White.toArgb()
        ) { darkMode == DarkMode.Dark || (darkMode == DarkMode.System && isSystemInDarkTheme) },
      )
      withDI(di = di) {
        App()
      }
    }
  }
}
