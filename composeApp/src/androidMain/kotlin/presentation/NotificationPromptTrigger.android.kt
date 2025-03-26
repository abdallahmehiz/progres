package presentation

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.delay
import mehiz.abdallah.progres.i18n.MR

@Composable
actual fun NotificationPromptTrigger(
  modifier: Modifier,
) {
  val context = LocalContext.current

  var hasNotificationsPermission by remember { mutableStateOf(true) }

  LaunchedEffect(Unit) {
    while (true) {
      hasNotificationsPermission = if (Build.VERSION.SDK_INT >= 33) {
        ContextCompat.checkSelfPermission(
          context,
          Manifest.permission.POST_NOTIFICATIONS,
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
      } else {
        true
      }
      delay(1000)
    }
  }

  val wasPreviouslyDenied = remember {
    mutableStateOf(
      if (Build.VERSION.SDK_INT >= 33) {
        ActivityCompat.shouldShowRequestPermissionRationale(
          context.findActivity(),
          Manifest.permission.POST_NOTIFICATIONS,
        )
      } else {
        false
      },
    )
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission(),
    onResult = { isGranted ->
      hasNotificationsPermission = isGranted
      if (!isGranted) {
        wasPreviouslyDenied.value = true
      }
    },
  )

  AnimatedVisibility(
    visible = !hasNotificationsPermission,
    modifier = modifier,
    fadeIn(),
    fadeOut()
  ) {
    Row(
      modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        .fillMaxWidth()
        .clickable {
          if (Build.VERSION.SDK_INT >= 33 && !wasPreviouslyDenied.value) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
          } else {
            val intent = Intent().apply {
              if (Build.VERSION.SDK_INT >= 26) {
                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
              } else {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", context.packageName, null)
              }
            }
            context.startActivity(intent)
          }
        }
        .padding(16.dp),
    ) {
      Icon(
        Icons.Rounded.NotificationsOff,
        contentDescription = stringResource(MR.strings.enable_notifications),
        tint = MaterialTheme.colorScheme.primary,
      )
      Text(
        stringResource(MR.strings.enable_notifications),
        modifier = Modifier.padding(horizontal = 8.dp),
      )
    }
  }
}

// Extension function to find the activity from a context
private fun android.content.Context.findActivity(): android.app.Activity {
  var context = this
  while (context is android.content.ContextWrapper) {
    if (context is android.app.Activity) return context
    context = context.baseContext
  }
  throw IllegalStateException("Couldn't find activity from context")
}
