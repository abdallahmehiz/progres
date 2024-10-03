package mehiz.abdallah.progres

import android.app.Application
import de.halfbit.logger.initializeLogger
import de.halfbit.logger.sink.android.registerAndroidLogSink
import de.halfbit.logger.sink.println.registerPrintlnSink
import di.initKoin
import mehiz.abdallah.progres.i18n.Localize
import org.koin.android.ext.koin.androidContext
import org.koin.androix.startup.KoinStartup.onKoinStartup

class App : Application() {

  init {
    onKoinStartup {
      androidContext(applicationContext)
      modules(initKoin(datastorePath = filesDir.path, localize = Localize(applicationContext)))
    }
  }

  override fun onCreate() {
    super.onCreate()
    Thread.setDefaultUncaughtExceptionHandler(GlobalExceptionHandler(applicationContext, CrashActivity::class.java))
    initializeLogger {
      registerPrintlnSink()
      registerAndroidLogSink()
    }
  }
}
