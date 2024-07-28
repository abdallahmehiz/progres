package mehiz.abdallah.progres

import App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import di.initKodein
import mehiz.abdallah.progres.i18n.Localize
import org.kodein.di.android.x.androidXModule
import org.kodein.di.compose.withDI

class MainActivity : ComponentActivity() {
  private val di by lazy {
    initKodein(
      applicationContext = androidXModule(application),
      datastorePath = applicationContext.filesDir.path,
      localize = Localize(this),
    )
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      withDI(di = di) {
        App()
      }
    }
  }
}
