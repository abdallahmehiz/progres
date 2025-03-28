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
import dev.icerock.moko.resources.desc.StringDesc
import org.koin.android.ext.android.inject
import org.koin.compose.koinInject
import preferences.BasePreferences
import preferences.preference.collectAsState
import presentation.theme.DarkMode
import utils.AuthRefreshWorker
import utils.PlatformUtils
import utils.UpdatesWorkers
import java.time.Duration
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
  private val platformUtils by inject<PlatformUtils>()
  private var isLoading by mutableStateOf(false)
  private val preferences by inject<BasePreferences>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    installSplashScreen().setKeepOnScreenCondition { isLoading }
    platformUtils.updateContext(this)
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

    UpdatesWorkers.scheduleCCGradesUpdateWork(this)
    UpdatesWorkers.scheduleExamGradesUpdateWork(this)
    UpdatesWorkers.scheduleTranscriptsUpdateWork(this)
    UpdatesWorkers.scheduleAppUpdateCheckWork(this)
    UpdatesWorkers.runAppUpdateCheckWorkImmediately(this)

    WorkManager
      .getInstance(applicationContext)
      .enqueueUniquePeriodicWork("auth_refresh", ExistingPeriodicWorkPolicy.UPDATE, authRefreshWorker)

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

  override fun onResume() {
    preferences.language.get().locale?.let {
      resources.configuration.setLocale(Locale(it))
      StringDesc.localeType = StringDesc.LocaleType.Custom(it)
    }
    super.onResume()
  }
}
