package presentation

import android.content.Intent
import android.net.Uri
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun OpenURIIconButton(
  icon: ImageVector,
  uri: String,
  modifier: Modifier,
  enabled: Boolean,
) {
  val context = LocalContext.current
  IconButton(
    onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri))) },
    modifier = modifier
  ) {
    Icon(icon, null)
  }
}
