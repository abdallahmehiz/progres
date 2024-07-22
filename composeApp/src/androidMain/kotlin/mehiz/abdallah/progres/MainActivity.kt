package mehiz.abdallah.progres

import App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import di.initKodein
import mehiz.abdallah.progres.i18n.Localize
import org.kodein.di.compose.withDI

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val di = initKodein(
      datastorePath = applicationContext.filesDir.path,
      localize = Localize(this)
    )
    setContent {
      withDI(di = di) {
        App()
      }
    }
  }
}
