package presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Suppress("ModifierWithoutDefault")
@Composable
expect fun SaveAndShareButtons(
  byteArray: ByteArray,
  fileName: String,
  modifier: Modifier
)

@Composable
fun BoxButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  content: @Composable () -> Unit
) {
  Box(
    modifier
      .fillMaxWidth(.5f)
      .clickable(onClick = onClick, enabled = enabled)
      .heightIn(48.dp),
    contentAlignment = Alignment.Center,
  ) {
    content()
  }
}