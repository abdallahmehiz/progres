package mehiz.abdallah.progres

import android.app.Application
import de.halfbit.logger.initializeLogger
import de.halfbit.logger.sink.println.registerPrintlnSink
import di.initKodein
import mehiz.abdallah.progres.i18n.Localize
import org.kodein.di.DIAware
import org.kodein.di.android.x.androidXModule

class App : Application(), DIAware {

  override val di by lazy {
    initKodein(
      applicationContext = androidXModule(this),
      datastorePath = applicationContext.filesDir.path,
      localize = Localize(this),
    )
  }

  override fun onCreate() {
    super.onCreate()
    Thread.setDefaultUncaughtExceptionHandler(GlobalExceptionHandler(applicationContext, CrashActivity::class.java))
    initializeLogger {
      registerPrintlnSink()
    }
  }
}
