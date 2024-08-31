package ui

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

actual val ScreenWidthPixels: @Composable () -> Int = {
  val dm = DisplayMetrics()
  val windowManager = LocalContext.current.getSystemService(Context.WINDOW_SERVICE) as WindowManager
  windowManager.defaultDisplay.getRealMetrics(dm)
  dm.widthPixels
}
actual val ScreenHeightPixels: @Composable () -> Int = {
  val dm = DisplayMetrics()
  val windowManager = LocalContext.current.getSystemService(Context.WINDOW_SERVICE) as WindowManager
  windowManager.defaultDisplay.getRealMetrics(dm)
  dm.heightPixels
}
