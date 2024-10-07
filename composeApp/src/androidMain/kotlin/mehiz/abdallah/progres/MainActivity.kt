package mehiz.abdallah.progres

import App
import android.os.Build
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
import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import org.koin.compose.koinInject
import preferences.BasePreferences
import preferences.preference.collectAsState
import presentation.theme.DarkMode
import utils.AuthRefreshWorker
import java.time.Duration
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
  private var isLoading by mutableStateOf(false)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    installSplashScreen().setKeepOnScreenCondition { isLoading }
    val authRefreshWorker = PeriodicWorkRequestBuilder<AuthRefreshWorker>(
      repeatInterval = 1,
      repeatIntervalTimeUnit = TimeUnit.DAYS,
    )
      .apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          setBackoffCriteria(BackoffPolicy.LINEAR, Duration.ofDays(7))
          setInitialDelay(Duration.ofDays(7))
        }
      }
      .build()

    WorkManager
      .getInstance(applicationContext)
      .enqueueUniquePeriodicWork(
        "auth_refresh",
        ExistingPeriodicWorkPolicy.UPDATE,
        authRefreshWorker,
      )

    setContent {
      val preferences = koinInject<BasePreferences>()
      val darkMode by preferences.darkMode.collectAsState()
      val isSystemInDarkTheme = isSystemInDarkTheme()
      enableEdgeToEdge(
        SystemBarStyle.auto(
          lightScrim = Color.Transparent.toArgb(),
          darkScrim = Color.Transparent.toArgb(),
        ) { darkMode == DarkMode.Dark || (darkMode == DarkMode.System && isSystemInDarkTheme) },
      )
      App(onReady = { isLoading = false })
    }
  }
}
