package mehiz.abdallah.progres

import android.app.Application
import com.liftric.kvault.KVault
import de.halfbit.logger.initializeLogger
import de.halfbit.logger.sink.android.registerAndroidLogSink
import de.halfbit.logger.sink.println.registerPrintlnSink
import di.initKoin
import mehiz.abdallah.progres.di.WorkersModule
import mehiz.abdallah.progres.i18n.Localize
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.androix.startup.KoinStartup.onKoinStartup
import org.koin.core.component.KoinComponent
import utils.CredentialManager

class App : Application(), KoinComponent {

  init {
    onKoinStartup {
      androidContext(applicationContext)
      workManagerFactory()
      modules(
        initKoin(
          datastorePath = filesDir.path,
          localize = Localize(applicationContext),
          credentialManager = CredentialManager(applicationContext),
          kVault = KVault(applicationContext)
        ),
        WorkersModule,
      )
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
