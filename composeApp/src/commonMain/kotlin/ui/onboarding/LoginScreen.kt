package ui.onboarding

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import mehiz.abdallah.progres.domain.AccountUseCase
import mehiz.abdallah.progres.i18n.MR
import org.kodein.di.compose.localDI
import org.kodein.di.instance
import presentation.WheelNumberPicker
import ui.home.HomeScreen

internal const val BAC_BEGINNING_YEAR = 1970

object LoginScreen : Screen {

  @Composable
  override fun Content() {
    val accountUseCase by localDI().instance<AccountUseCase>()

    LoginScreen(
      onLoginPressed = { id, password -> accountUseCase.login(id, password) },
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
  onLoginPressed: suspend (String, String) -> Unit,
  modifier: Modifier = Modifier,
) {
  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {},
        actions = {
          var showLanguageDropDown by remember { mutableStateOf(false) }
          IconButton(
            onClick = { showLanguageDropDown = true },
          ) {
            Icon(
              Icons.Outlined.Translate,
              "change language",
            )
          }
          DropdownMenu(
            expanded = showLanguageDropDown,
            onDismissRequest = { showLanguageDropDown = false },
          ) {
            listOf(
              MR.strings.lang_ar,
              MR.strings.lang_en,
              MR.strings.lang_fr
            ).forEach {
              DropdownMenuItem(
                text = {
                  Text(
                    stringResource(it),
                    textAlign = if (it == MR.strings.lang_ar) TextAlign.End else TextAlign.Start
                  )
                },
                onClick = {
                  StringDesc.localeType = StringDesc.LocaleType.Custom(
                    when (it) {
                      MR.strings.lang_ar -> "ar"
                      MR.strings.lang_en -> "en"
                      MR.strings.lang_fr -> "fr"
                      else -> "en"
                    }
                  )
                }
              )
            }
          }
        },
      )
    },
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(paddingValues)
        .padding(horizontal = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Image(
        painter = painterResource(MR.images.progres),
        null,
      )
      var id by remember { mutableStateOf("") }
      var year by rememberSaveable {
        mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year.toString())
      }
      val yearsRange = BAC_BEGINNING_YEAR..year.toInt()
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
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        OutlinedTextField(
          value = year,
          onValueChange = { year = it },
          label = { Text(stringResource(MR.strings.onboarding_year_textfield_label)) },
          leadingIcon = {
            IconButton(
              onClick = { showYearPickerAlert = true },
            ) {
              Icon(
                Icons.Outlined.DateRange,
                "open date picker",
              )
            }
          },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier.weight(2f),
        )
        OutlinedTextField(
          value = id,
          onValueChange = { id = it },
          label = { Text(stringResource(MR.strings.onboarding_id_textfield_label)) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          leadingIcon = { Icon(Icons.Outlined.Person, null) },
          modifier = Modifier.weight(3f),
        )
      }
      var password by rememberSaveable { mutableStateOf("") }
      var isPasswordVisible by remember { mutableStateOf(false) }
      OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text(stringResource(MR.strings.onboarding_password_textfield_label)) },
        leadingIcon = { Icon(Icons.Outlined.Key, null) },
        trailingIcon = {
          IconButton(
            onClick = {
              isPasswordVisible = !isPasswordVisible
            },
          ) {
            Icon(
              if (isPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
              "toggle password visibility",
            )
          }
        },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
      )
      var isLoadingIndicatorShown by remember { mutableStateOf(false) }
      val navigator = LocalNavigator.currentOrThrow
      val scope = rememberCoroutineScope()
      Button(
        onClick = {
          isLoadingIndicatorShown = true
          scope.launch(Dispatchers.IO) {
            try {
              onLoginPressed(year + id, password)
              navigator.replaceAll(HomeScreen)
            } catch (e: Exception) {
              e.printStackTrace()
              isLoadingIndicatorShown = false
            }
          }
        },
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          modifier = Modifier.animateContentSize(),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          if (isLoadingIndicatorShown) {
            CircularProgressIndicator(
              modifier = Modifier.size(24.dp),
              color = MaterialTheme.colorScheme.onPrimary,
              strokeWidth = 2.dp,
            )
          } else {
            Text(stringResource(MR.strings.onboarding_login_button))
          }
        }
      }
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
    modifier = modifier
  ) {
    Surface(
      modifier = Modifier.fillMaxWidth(),
      color = MaterialTheme.colorScheme.surface,
      shape = AlertDialogDefaults.shape,
      tonalElevation = AlertDialogDefaults.TonalElevation,
    ) {
      Column(
        modifier = Modifier
          .padding(16.dp),
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
