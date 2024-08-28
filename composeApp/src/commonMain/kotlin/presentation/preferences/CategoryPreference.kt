package presentation.preferences

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CategoryPreference(
  title: String,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.labelLarge,
      color = MaterialTheme.colorScheme.secondary
    )
  }
}
