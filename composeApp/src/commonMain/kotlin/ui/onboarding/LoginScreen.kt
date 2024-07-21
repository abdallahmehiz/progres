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
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import mehiz.abdallah.progres.domain.AccountUseCase
import org.jetbrains.compose.resources.painterResource
import org.kodein.di.compose.localDI
import org.kodein.di.instance
import presentation.WheelNumberPicker
import progres.composeapp.generated.resources.Res
import progres.composeapp.generated.resources.progres_dark
import ui.home.HomeScreen

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
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
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
          DropdownMenu(expanded = showLanguageDropDown, onDismissRequest = { showLanguageDropDown = false }) {
            Text("Arabic")
            Text("English")
            Text("French")
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
        painter = painterResource(Res.drawable.progres_dark),
        null,
      )
      var id by remember { mutableStateOf("") }
      var year by rememberSaveable {
        mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year.toString())
      }
      val yearsRange = 1970..year.toInt()
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
          label = { Text("year") },
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
          label = { Text("ID Number") },
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
        label = { Text("Password") },
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
            Text("Login")
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
) {
  var index by remember {
    mutableStateOf(
      runCatching { range.indexOf(value) }.getOrNull() ?: range.indexOf(range.last),
    )
  }
  BasicAlertDialog(
    onDismissRequest = onDismissRequest,
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
          "Select year",
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
            Text("Cancel")
          }
          TextButton(
            onClick = { onValueChanged(range.elementAt(index)) },
          ) {
            Text("Ok")
          }
        }
      }
    }
  }
}
