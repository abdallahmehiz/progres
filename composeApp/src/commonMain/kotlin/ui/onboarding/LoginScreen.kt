package ui.onboarding

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dokar.sonner.Toast
import com.dokar.sonner.ToastType
import com.dokar.sonner.ToasterState
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import di.ScreenModelsModule
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import mehiz.abdallah.progres.domain.UserAuthUseCase
import mehiz.abdallah.progres.i18n.MR
import org.koin.compose.koinInject
import org.koin.core.context.loadKoinModules
import preferences.BasePreferences
import preferences.Language
import preferences.preference.collectAsState
import presentation.WheelNumberPicker
import presentation.preferences.PreferenceFooter
import presentation.theme.DarkMode
import ui.home.HomeScreen
import utils.CredentialManager

internal const val BAC_BEGINNING_YEAR = 1970

object LoginScreen : Screen {

  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val accountUseCase = koinInject<UserAuthUseCase>()
    val basePreferences = koinInject<BasePreferences>()

    LoginScreen(
      onLoginPressed = { id, password ->
        accountUseCase.login(id, password)
        loadKoinModules(ScreenModelsModule)
        basePreferences.isLoggedIn.set(true)
        navigator.replaceAll(HomeScreen)
      },
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Suppress("CyclomaticComplexMethod")
@Composable
fun LoginScreen(
  onLoginPressed: suspend (String, String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val scope = rememberCoroutineScope()
  val credentialManager = koinInject<CredentialManager>()
  val preferences = koinInject<BasePreferences>()
  val toaster = koinInject<ToasterState>()
  var id by rememberSaveable { mutableStateOf("") }
  var year by remember { mutableStateOf("") }
  var password by rememberSaveable { mutableStateOf("") }
  var isLoadingIndicatorShown by remember { mutableStateOf(false) }
  LaunchedEffect(Unit) {
    credentialManager.signIn()?.let {
      year = it.first.substring(0..3)
      id = it.first.substring(4)
      password = it.second
      try {
        isLoadingIndicatorShown = true
        onLoginPressed(it.first, it.second)
      } catch (e: Exception) {
        isLoadingIndicatorShown = false
      }
    }
  }
  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {},
        actions = {
          var showLanguageDropDown by remember { mutableStateOf(false) }
          val locale by preferences.language.collectAsState()
          IconButton(
            onClick = { showLanguageDropDown = true },
          ) { Icon(Icons.Outlined.Translate, "change language") }
          DropdownMenu(
            expanded = showLanguageDropDown,
            onDismissRequest = { showLanguageDropDown = false },
          ) {
            Language.entries.forEach {
              DropdownMenuItem(
                text = { Text(stringResource(it.string)) },
                leadingIcon = { if (locale == it) Icon(Icons.Rounded.Check, null) },
                onClick = { preferences.language.set(it) },
              )
            }
          }
        },
        windowInsets = WindowInsets(0.dp),
      )
    },
  ) { paddingValues ->
    Column(
      modifier = Modifier.fillMaxWidth().padding(paddingValues).padding(horizontal = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Box {
        val darkMode by preferences.darkMode.collectAsState()
        if (darkMode == DarkMode.Dark || (isSystemInDarkTheme() && darkMode == DarkMode.System)) {
          Icon(
            painter = painterResource(MR.images.progres),
            null,
            modifier = Modifier.blur(4.dp),
          )
        }
        Image(
          painter = painterResource(MR.images.progres),
          null,
        )
      }

      val yearsRange = BAC_BEGINNING_YEAR..Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
      var showYearPickerAlert by remember { mutableStateOf(false) }
      if (showYearPickerAlert) {
        val onDismissRequest: () -> Unit = { showYearPickerAlert = false }
        YearPickerAlert(
          value = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year,
          onValueChanged = {
            year = it.toString()
            onDismissRequest()
          },
          range = yearsRange,
          onDismissRequest = onDismissRequest,
        )
      }
      val yearFocusRequester = FocusRequester()
      val idFocusRequester = FocusRequester()
      val passwordFocusRequester = FocusRequester()
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        OutlinedTextField(
          value = year,
          onValueChange = {
            if (it.length > 4) return@OutlinedTextField
            year = it
            if (year.length == 4) idFocusRequester.requestFocus()
          },
          label = { Text(stringResource(MR.strings.onboarding_year_textfield_label)) },
          leadingIcon = {
            IconButton(onClick = { showYearPickerAlert = true }) {
              Icon(Icons.Outlined.DateRange, "open date picker")
            }
          },
          placeholder = { Text(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year.toString()) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier.weight(2f).focusRequester(yearFocusRequester),
        )
        OutlinedTextField(
          value = id,
          onValueChange = {
            if (it.length > 8) return@OutlinedTextField
            id = it
            if (it.length == 8) passwordFocusRequester.requestFocus()
          },
          label = {
            Text(
              stringResource(MR.strings.onboarding_id_textfield_label),
              maxLines = 1,
            )
          },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          leadingIcon = { Icon(Icons.Outlined.Person, null) },
          modifier = Modifier.weight(3f).focusRequester(idFocusRequester),
        )
      }
      var isPasswordVisible by remember { mutableStateOf(false) }
      OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text(stringResource(MR.strings.onboarding_password_textfield_label)) },
        leadingIcon = { Icon(Icons.Outlined.Key, null) },
        trailingIcon = {
          IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
            Icon(
              if (isPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
              "toggle password visibility",
            )
          }
        },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        modifier = Modifier.fillMaxWidth().focusRequester(passwordFocusRequester),
      )
      Button(
        onClick = {
          isLoadingIndicatorShown = true
          scope.launch(Dispatchers.IO) {
            try {
              onLoginPressed("$year$id", password)
              GlobalScope.launch(NonCancellable) {
                credentialManager.signUp("$year$id", password)
              }
            } catch (e: Exception) {
              toaster.show(Toast(e.message!!, type = ToastType.Error))
              isLoadingIndicatorShown = false
            }
          }
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = !isLoadingIndicatorShown,
      ) {
        Row(
          modifier = Modifier.animateContentSize(),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          if (isLoadingIndicatorShown) {
            CircularProgressIndicator(
              modifier = Modifier.size(24.dp),
              color = LocalContentColor.current,
              strokeWidth = 2.dp,
            )
          } else {
            Text(stringResource(MR.strings.onboarding_login_button))
          }
        }
      }
      Spacer(Modifier.weight(1f))
      PreferenceFooter(stringResource(MR.strings.alert_unofficial_app_note))
      Spacer(Modifier.height(16.dp))
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearPickerAlert(
  value: Int,
  onValueChanged: (Int) -> Unit,
  range: IntRange,
  onDismissRequest: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var index by remember {
    mutableStateOf(
      runCatching { range.indexOf(value) }.getOrNull() ?: range.indexOf(range.last),
    )
  }
  BasicAlertDialog(
    onDismissRequest = onDismissRequest,
    modifier = modifier,
  ) {
    Surface(
      modifier = Modifier.fillMaxWidth(),
      color = MaterialTheme.colorScheme.surface,
      shape = AlertDialogDefaults.shape,
      tonalElevation = AlertDialogDefaults.TonalElevation,
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        Text(
          stringResource(MR.strings.onboarding_select_year_picker),
          style = MaterialTheme.typography.headlineSmall,
          modifier = Modifier.padding(horizontal = 16.dp),
        )
        WheelNumberPicker(
          startIndex = index,
          items = range.toImmutableList(),
          onSelectionChanged = { index = it },
          modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
        ) {
          TextButton(
            onClick = onDismissRequest,
          ) {
            Text(stringResource(MR.strings.generic_cancel))
          }
          TextButton(
            onClick = { onValueChanged(range.elementAt(index)) },
          ) {
            Text(stringResource(MR.strings.generic_ok))
          }
        }
      }
    }
  }
}
