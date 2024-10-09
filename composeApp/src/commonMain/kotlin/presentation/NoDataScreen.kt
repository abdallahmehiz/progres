package presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.readTextAsState
import dev.icerock.moko.resources.compose.stringResource
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import mehiz.abdallah.progres.i18n.MR

@Composable
fun NoDataScreen(
  modifier: Modifier = Modifier,
) {
  Column(
    modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.SpaceAround,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        stringResource(MR.strings.no_data_screen_title),
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center,
      )
      Text(
        stringResource(MR.strings.no_data_screen_subtitle),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
      )
    }
    val json by MR.files.lottie_empty_json.readTextAsState()
    val composition by rememberLottieComposition { LottieCompositionSpec.JsonString(json!!) }
    val progress by animateLottieCompositionAsState(composition)
    Image(
      painter = rememberLottiePainter(
        composition = composition,
        progress = { progress },
      ),
      contentDescription = "Lottie animation",
    )
  }
}
