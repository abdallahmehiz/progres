package presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dokar.sonner.Toast
import com.dokar.sonner.ToastType
import dev.icerock.moko.resources.compose.stringResource
import mehiz.abdallah.progres.i18n.MR
import ui.crash.LogsContainer

@Composable
fun ErrorScreenContent(
  throwable: Throwable,
  modifier: Modifier = Modifier
) {
  Column(
    modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Row(
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Icon(
        Icons.Outlined.BugReport,
        null,
        modifier = Modifier
          .size(96.dp)
      )
    }
    Text(
      stringResource(MR.strings.error_screen_oops),
      style = MaterialTheme.typography.headlineMedium
    )
    Text(
      throwable.message!!,
      style = MaterialTheme.typography.bodyLarge
    )
    LogsContainer(throwable.stackTraceToString())
  }
}

fun errorToast(message: String) = Toast(message, type = ToastType.Error)
