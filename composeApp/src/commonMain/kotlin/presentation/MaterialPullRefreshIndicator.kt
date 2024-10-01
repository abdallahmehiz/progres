package presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.materii.pullrefresh.PullRefreshIndicator
import dev.materii.pullrefresh.PullRefreshState

@Composable
fun MaterialPullRefreshIndicator(
  state: PullRefreshState,
  modifier: Modifier = Modifier,
) {
  PullRefreshIndicator(
    state,
    backgroundColor = MaterialTheme.colorScheme.surfaceContainerHighest,
    contentColor = MaterialTheme.colorScheme.onSurface,
    modifier = modifier
  )
}
