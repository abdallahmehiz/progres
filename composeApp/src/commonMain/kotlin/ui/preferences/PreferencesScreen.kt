package ui.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSwitch
import com.liftric.kvault.KVault
import compose.icons.SimpleIcons
import compose.icons.simpleicons.Github
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import mehiz.abdallah.progres.domain.AccountUseCase
import mehiz.abdallah.progres.i18n.MR
import org.koin.compose.koinInject
import preferences.BasePreferences
import preferences.Language
import preferences.preference.collectAsState
import preferences.preference.toggle
import presentation.OpenURIIconButton
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
            IconButton(onClick = navigator::pop) {
              Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
            }
          },
          windowInsets = WindowInsets(0.dp)
        )
      },
    ) { paddingValues ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues = paddingValues),
      ) {
        CategoryPreference(title = stringResource(MR.strings.pref_appearance))
        val darkMode by preferences.darkMode.collectAsState()
        MultiChoiceSegmentedButtonsPreference(
          DarkMode.entries.toImmutableList(),
          valueToText = { it.name },
          selectedIndices = persistentListOf(DarkMode.entries.indexOf(darkMode)),
          onClick = { preferences.darkMode.set(it) },
        )
        val materialYou by preferences.materialYou.collectAsState()
        SettingsSwitch(
          state = materialYou,
          title = { Text(stringResource(MR.strings.material_you)) },
          subtitle = { if (!isMaterialYouAvailable) Text(stringResource(MR.strings.material_you_unavailable)) },
          enabled = isMaterialYouAvailable,
          onCheckedChange = { preferences.materialYou.toggle() },
        )
        Box {
          val language by preferences.language.collectAsState()
          var isLanguageDropDownShown by remember { mutableStateOf(false) }
          SettingsMenuLink(
            title = { Text(stringResource(MR.strings.pref_language)) },
            onClick = { isLanguageDropDownShown = true },
            subtitle = { Text(stringResource(language.string)) },
          )
          DropdownMenu(isLanguageDropDownShown, { isLanguageDropDownShown = false }) {
            Language.entries.forEach {
              DropdownMenuItem(
                text = { Text(stringResource(it.string)) },
                leadingIcon = {
                  if (it == language) Icon(Icons.Rounded.Check, null)
                },
                onClick = { preferences.language.set(it) },
              )
            }
          }
        }
        val accountUseCase = koinInject<AccountUseCase>()
        val kVault = koinInject<KVault>()
        SettingsMenuLink(
          title = { Text(stringResource(MR.strings.pref_logout)) },
          onClick = {
            scope.launch(Dispatchers.IO) {
              accountUseCase.logout()
              preferences.isLoggedIn.set(false)
              kVault.clear()
              navigator.replaceAll(LoginScreen)
            }
          },
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center
        ) {
          OpenURIIconButton(SimpleIcons.Github, stringResource(MR.strings.repository_url), modifier = Modifier)
        }
      }
    }
  }
}
