package ui.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.alorma.compose.settings.ui.SettingsSwitch
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.kodein.di.compose.localDI
import org.kodein.di.instance
import preferences.BasePreferences
import preferences.preference.collectAsState
import preferences.preference.toggle
import presentation.preferences.CategoryPreference
import presentation.preferences.MultiChoiceSegmentedButtonsPreference
import presentation.theme.DarkMode
import presentation.theme.isMaterialYouAvailable

object PreferencesScreen : Screen {

  override val key = uniqueScreenKey

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val preferences by localDI().instance<BasePreferences>()
    Scaffold(
      topBar = {
        TopAppBar(
          title = {
            Text("Preferences")
          },
          navigationIcon = {
            IconButton(onClick = { navigator.pop() }) {
              Icon(Icons.AutoMirrored.Default.ArrowBack, null)
            }
          }
        )
      }
    ) { paddingValues ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues = paddingValues)
      ) {
        CategoryPreference(title = "Appearance")
        val darkMode by preferences.darkMode.collectAsState()
        MultiChoiceSegmentedButtonsPreference(
          DarkMode.entries.toImmutableList(),
          valueToText = { it.name },
          selectedIndices = persistentListOf(DarkMode.entries.indexOf(darkMode)),
          onClick = { preferences.darkMode.set(it) }
        )
        val materialYou by preferences.materialYou.collectAsState()
        SettingsSwitch(
          state = materialYou,
          title = { Text("Material You") },
          enabled = isMaterialYouAvailable,
          onCheckedChange = { preferences.materialYou.toggle() }
        )
      }
    }
  }
}
