import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import org.jetbrains.compose.ui.tooling.preview.Preview
import ui.onboarding.LoginScreen

@Composable
@Preview
fun App() {
  MaterialTheme {
    Navigator(
      screen = LoginScreen
    )
  }
}
