package ui.home.restaurant

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey

object RestaurantScreen : Screen {

  override val key = uniqueScreenKey

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun Content() {
    /*
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
     */
  }
}
