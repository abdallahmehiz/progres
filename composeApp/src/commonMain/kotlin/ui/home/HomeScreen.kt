package ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import cafe.adriel.voyager.core.screen.Screen

object HomeScreen : Screen {
  @Composable
  override fun Content() {
    Box(contentAlignment = Alignment.Center) {
      Text("Home Screen")
    }
  }
}
