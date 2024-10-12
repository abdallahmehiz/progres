package mehiz.abdallah.progres

import android.app.Application
import com.google.firebase.FirebaseApp
import com.liftric.kvault.KVault
import de.halfbit.logger.initializeLogger
import de.halfbit.logger.sink.android.registerAndroidLogSink
import di.initKoin
import mehiz.abdallah.progres.di.WorkersModule
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.androix.startup.KoinStartup.onKoinStartup
import org.koin.core.component.KoinComponent
import utils.CredentialManager
import utils.PlatformUtils

class App : Application(), KoinComponent {

  init {
    onKoinStartup {
      androidContext(applicationContext)
      workManagerFactory()
      modules(
        initKoin(
          datastorePath = filesDir.path,
          credentialManager = CredentialManager(applicationContext),
          kVault = KVault(applicationContext),
          platformUtils = PlatformUtils(applicationContext)
        ),
        WorkersModule,
      )
    }
  }

  override fun onCreate() {
    super.onCreate()
    FirebaseApp.initializeApp(applicationContext)
    Thread.setDefaultUncaughtExceptionHandler(GlobalExceptionHandler(applicationContext, CrashActivity::class.java))
    initializeLogger { registerAndroidLogSink() }
  }
}
