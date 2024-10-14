package presentation

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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

actual val ScreenHeightDp: @Composable () -> Dp = {
  LocalConfiguration.current.screenHeightDp.dp
}

actual val ScreenWidthDp: @Composable () -> Dp = {
  LocalConfiguration.current.screenWidthDp.dp
}
