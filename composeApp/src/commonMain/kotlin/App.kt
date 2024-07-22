import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import ui.onboarding.LoginScreen

@Composable
fun App() {
  MaterialTheme {
    Navigator(
      screen = LoginScreen
    )
  }
}
