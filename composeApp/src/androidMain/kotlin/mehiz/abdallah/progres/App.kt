package mehiz.abdallah.progres

import android.app.Application
import com.google.firebase.FirebaseApp
import di.initKoin
import mehiz.abdallah.progres.di.WorkersModule
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import utils.CredentialManager
import utils.PlatformUtils

class App : Application(), KoinComponent {

  override fun onCreate() {
    super.onCreate()

    startKoin {
      androidContext(applicationContext)
      workManagerFactory()
      modules(
        initKoin(
          datastorePath = filesDir.path,
          credentialManager = CredentialManager(applicationContext),
          platformUtils = PlatformUtils(applicationContext)
        ),
        WorkersModule,
      )
    }
    FirebaseApp.initializeApp(applicationContext)
    Thread.setDefaultUncaughtExceptionHandler(GlobalExceptionHandler(applicationContext, CrashActivity::class.java))
  }
}
