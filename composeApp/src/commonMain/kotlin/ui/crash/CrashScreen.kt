package ui.crash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import mehiz.abdallah.progres.i18n.MR

@Composable
fun CrashScreen(
  exceptionString: String,
  logs: String,
  onDumpLogs: (logs: String) -> Unit,
  onCopyLogs: (logs: String) -> Unit,
  onAppRestart: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val scope = rememberCoroutineScope()
  Scaffold(
    modifier = modifier.fillMaxSize(),
    bottomBar = {
      val borderColor = MaterialTheme.colorScheme.outline
      Column(
        Modifier.windowInsetsPadding(NavigationBarDefaults.windowInsets).drawBehind {
          drawLine(
            borderColor,
            Offset.Zero,
            Offset(size.width, 0f),
            strokeWidth = Dp.Hairline.value,
          )
        }.padding(vertical = 8.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Button(
            onClick = {
              scope.launch(Dispatchers.IO) {
                onDumpLogs(
                  concatLogs(
                    deviceInfo = collectDeviceInfo(),
                    exception = exceptionString,
                    logs = logs,
                  ),
                )
              }
            },
            modifier = Modifier.weight(1f),
          ) { Text(stringResource(MR.strings.crash_screen_share)) }
          FilledIconButton(
            onClick = { onCopyLogs(concatLogs(collectDeviceInfo(), exceptionString, logs)) },
          ) {
            Icon(Icons.Default.ContentCopy, null)
          }
        }
        OutlinedButton(
          onClick = onAppRestart,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Text(stringResource(MR.strings.crash_screen_restart))
        }
      }
    },
  ) { paddingValues ->
    Column(
      modifier = Modifier.padding(paddingValues).padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Spacer(Modifier.height(paddingValues.calculateTopPadding()))
      Icon(
        Icons.Outlined.BugReport,
        null,
        modifier = Modifier.size(48.dp),
        tint = MaterialTheme.colorScheme.primary,
      )
      Text(
        stringResource(MR.strings.crash_screen_title),
        style = MaterialTheme.typography.headlineLarge,
      )
      Text(
        stringResource(MR.strings.crash_screen_subtitle, stringResource(MR.strings.app_name)),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Text(
        stringResource(MR.strings.crash_screen_logs_title),
        style = MaterialTheme.typography.headlineSmall,
      )
      LogsContainer(exceptionString)
      Text(
        stringResource(MR.strings.crash_screen_logs_title),
        style = MaterialTheme.typography.headlineSmall,
      )
      LogsContainer(logs)
      Spacer(Modifier.height(8.dp))
    }
  }
}

@Composable
fun LogsContainer(
  logs: String,
  modifier: Modifier = Modifier,
) {
  LazyRow(
    modifier = modifier.clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
  ) {
    item {
      SelectionContainer {
        Text(
          text = logs,
          fontFamily = FontFamily.Monospace,
          style = MaterialTheme.typography.labelMedium,
          modifier = Modifier.padding(8.dp),
        )
      }
    }
  }
}

private fun concatLogs(
  deviceInfo: String,
  exception: String,
  logs: String,
) = "$deviceInfo\n\nException:\n$exception\n\nLogs:\n$logs"

expect fun collectLogs(): String

expect fun collectDeviceInfo(): String
