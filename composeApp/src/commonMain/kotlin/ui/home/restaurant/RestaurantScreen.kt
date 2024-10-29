package ui.home.restaurant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ConfirmationNumber
import androidx.compose.material.icons.rounded.DinnerDining
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dokar.sonner.Toast
import com.dokar.sonner.ToasterState
import dev.icerock.moko.resources.compose.stringResource
import mehiz.abdallah.progres.i18n.MR
import org.koin.compose.koinInject
import org.publicvalue.multiplatform.qrcode.CodeType
import org.publicvalue.multiplatform.qrcode.ScannerWithPermissions

object RestaurantScreen : Screen {

  override val key = uniqueScreenKey

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val toasterState = koinInject<ToasterState>()
    Scaffold(
      topBar = {
        TopAppBar(
          title = {
            Text(stringResource(MR.strings.home_restaurant))
          },
          navigationIcon = {
            IconButton(onClick = navigator::pop) {
              Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
            }
          },
          windowInsets = WindowInsets(0.dp),
        )
      },
    ) { paddingValues ->
      Column(
        modifier = Modifier
          .padding(paddingValues)
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceAround,
      ) {
        ScannerWithPermissions(
          onScanned = {
            toasterState.show(Toast(it))
            true
          },
          types = listOf(CodeType.QR),
          modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.3f)
            .clip(RoundedCornerShape(16.dp)),
        )
        Column(
          modifier = Modifier,
          verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          Step(
            title = stringResource(MR.strings.restaurant_step_scan_title),
            subtitle = stringResource(MR.strings.restaurant_step_scan_subtitle),
            icon = Icons.Rounded.QrCodeScanner,
            stepNumber = 1
          )
          Step(
            title = stringResource(MR.strings.restaurant_step_ticket_title),
            subtitle = stringResource(MR.strings.restaurant_step_ticket_subtitle),
            icon = Icons.Rounded.ConfirmationNumber,
            stepNumber = 2
          )
          Step(
            title = stringResource(MR.strings.restaurant_step_enjoy_title),
            icon = Icons.Rounded.DinnerDining,
            stepNumber = 3
          )
        }
      }
    }
  }

  @Composable
  fun Step(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    stepNumber: Int? = null,
    subtitle: String? = null,
  ) {
    Column(
      modifier = modifier,
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      if (stepNumber != null) {
        Text(
          stringResource(MR.strings.step_formatted, stepNumber),
          style = MaterialTheme.typography.headlineMedium
        )
      }
      Row(
        modifier = Modifier
          .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Icon(
          icon,
          null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier
            .size(72.dp),
        )
        Column(
          verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Text(
            title,
            style = MaterialTheme.typography.headlineSmall,
          )
          if (subtitle != null) {
            Text(
              subtitle,
              style = MaterialTheme.typography.bodyMedium,
            )
          }
        }
      }
    }
  }
}
