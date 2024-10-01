package presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Suppress("ModifierWithoutDefault")
@Composable
expect fun OpenURIIconButton(
  icon: ImageVector,
  uri: String,
  modifier: Modifier,
  enabled: Boolean = true,
)
