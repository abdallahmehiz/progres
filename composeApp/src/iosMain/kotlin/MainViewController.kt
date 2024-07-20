import androidx.compose.ui.window.ComposeUIViewController
import di.initKodein
import kotlinx.cinterop.ExperimentalForeignApi
import org.kodein.di.compose.withDI
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.UIKit.UIViewController
import preferences.DataStoreFileName

fun MainViewController(): UIViewController {
  val di = initKodein(preferencesStorePath())
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
