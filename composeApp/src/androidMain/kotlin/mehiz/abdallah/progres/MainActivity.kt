package mehiz.abdallah.progres

import App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import di.initKodein
import org.kodein.di.compose.withDI

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val di = initKodein(applicationContext.filesDir.path)
    setContent {
      withDI(di = di) {
        App()
      }
    }
  }
}

@Preview
@Composable
fun AppAndroidPreview() {
  App()
}
