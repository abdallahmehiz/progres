package presentation.theme

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

actual val isMaterialYouAvailable: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

@SuppressLint("NewApi")
actual val MaterialYouColorScheme: @Composable (Boolean) -> ColorScheme = {
  if (it) {
    dynamicDarkColorScheme(LocalContext.current)
  } else {
    dynamicLightColorScheme(LocalContext.current)
  }
}
