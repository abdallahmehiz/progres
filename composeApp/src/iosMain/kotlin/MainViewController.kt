import androidx.compose.ui.window.ComposeUIViewController
import di.initKoin
import kotlinx.cinterop.ExperimentalForeignApi
import mehiz.abdallah.progres.i18n.Localize
import org.kodein.di.compose.withDI
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.UIKit.UIViewController

@Suppress("FunctionNaming")
fun MainViewController(): UIViewController {
  val di = initKoin(
    datastorePath = preferencesStorePath(),
    localize = Localize()
  )
  return ComposeUIViewController {
    withDI(di = di) {
      App()
    }
  }
}

@OptIn(ExperimentalForeignApi::class)
val preferencesStorePath: () -> String = {
  requireNotNull(
    NSFileManager.defaultManager.URLForDirectory(
      directory = NSDocumentDirectory,
      inDomain = NSUserDomainMask,
      appropriateForURL = null,
      create = false,
      error = null,
    ),
  ).path!!
}
