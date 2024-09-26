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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSwitch
import dev.icerock.moko.resources.compose.stringResource
import di.ViewModelsModule
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mehiz.abdallah.progres.domain.AccountUseCase
import mehiz.abdallah.progres.i18n.MR
import org.koin.compose.koinInject
import org.koin.core.context.unloadKoinModules
import preferences.BasePreferences
import preferences.preference.collectAsState
import preferences.preference.toggle
import presentation.preferences.CategoryPreference
import presentation.preferences.MultiChoiceSegmentedButtonsPreference
import presentation.theme.DarkMode
import presentation.theme.isMaterialYouAvailable
import ui.onboarding.LoginScreen

object PreferencesScreen : Screen {

  override val key = uniqueScreenKey

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val scope = rememberCoroutineScope()
    val preferences = koinInject<BasePreferences>()
    Scaffold(
      topBar = {
        TopAppBar(
          title = {
            Text(stringResource(MR.strings.pref_title))
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
        CategoryPreference(title = stringResource(MR.strings.pref_appearance))
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
          title = { Text(stringResource(MR.strings.material_you)) },
          subtitle = { if (!isMaterialYouAvailable) Text(stringResource(MR.strings.material_you_unavailable)) },
          enabled = isMaterialYouAvailable,
          onCheckedChange = { preferences.materialYou.toggle() }
        )
        val accountUseCase = koinInject<AccountUseCase>()
        SettingsMenuLink(
          title = { Text(stringResource(MR.strings.pref_logout)) },
          onClick = {
            scope.launch(Dispatchers.IO) {
              accountUseCase.logout()
              navigator.replaceAll(LoginScreen)
              delay(1000) // just to ensure the home screen disappears
              unloadKoinModules(ViewModelsModule)
            }
          }
        )
      }
    }
  }
}
