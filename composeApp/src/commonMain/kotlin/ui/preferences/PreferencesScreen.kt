package ui.preferences

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Translate
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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSwitch
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
import presentation.preferences.CategoryPreference
import presentation.preferences.MultiChoiceSegmentedButtonsPreference
import presentation.theme.DarkMode
import presentation.theme.isMaterialYouAvailable
import ui.onboarding.LoginScreen
import utils.PlatformUtils
import utils.ToastLength

object PreferencesScreen : Screen {

  override val key = uniqueScreenKey

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val scope = rememberCoroutineScope()
    val preferences = koinInject<BasePreferences>()
    val platformUtils = koinInject<PlatformUtils>()
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
          valueToText = { platformUtils.getString(it.stringRes) },
          selectedIndices = persistentListOf(DarkMode.entries.indexOf(darkMode)),
          onClick = preferences.darkMode::set,
        )
        val materialYou by preferences.materialYou.collectAsState()
        SettingsSwitch(
          state = materialYou,
          title = { Text(stringResource(MR.strings.material_you)) },
          subtitle = {
            Text(
              stringResource(
                if (isMaterialYouAvailable) MR.strings.material_you_subtitle else MR.strings.material_you_unavailable
              ),
            )
          },
          icon = { Icon(Icons.Rounded.Palette, null) },
          enabled = isMaterialYouAvailable,
          onCheckedChange = { preferences.materialYou.toggle() },
        )
        Box {
          val language by preferences.language.collectAsState()
          var isLanguageDropDownShown by remember { mutableStateOf(false) }
          SettingsMenuLink(
            title = { Text(stringResource(MR.strings.pref_language)) },
            onClick = { isLanguageDropDownShown = true },
            icon = { Icon(Icons.Rounded.Translate, null) },
            subtitle = { Text(stringResource(language.string)) },
          )
          DropdownMenu(isLanguageDropDownShown, { isLanguageDropDownShown = false }) {
            Language.entries.forEach {
              DropdownMenuItem(
                text = { Text(stringResource(it.string)) },
                leadingIcon = { if (it == language) Icon(Icons.Rounded.Check, null) },
                onClick = {
                  preferences.language.set(it)
                  platformUtils.toast(platformUtils.getString(MR.strings.pref_requires_restart), ToastLength.Short)
                },
              )
            }
          }
        }
        val accountUseCase = koinInject<AccountUseCase>()
        SettingsMenuLink(
          title = { Text(stringResource(MR.strings.pref_logout)) },
          subtitle = { Text(stringResource(MR.strings.pref_logout_subtitle)) },
          icon = { Icon(Icons.AutoMirrored.Rounded.Logout, null) },
          onClick = {
            scope.launch(Dispatchers.IO) {
              accountUseCase.logout()
              preferences.isLoggedIn.set(false)
              navigator.replaceAll(LoginScreen)
            }
          },
        )
        SettingsMenuLink(
          title = { Text(stringResource(MR.strings.about_title)) },
          subtitle = { Text(stringResource(MR.strings.about_subtitle)) },
          icon = { Icon(Icons.Rounded.Info, null) },
          onClick = { navigator.push(AboutScreen) },
        )
      }
    }
  }
}
