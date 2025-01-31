/*
Copyright © 2015 Javier Tomás
Copyright © 2024 Mihon Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMaxBy
import dev.icerock.moko.resources.compose.stringResource
import mehiz.abdallah.progres.i18n.MR
import org.koin.compose.koinInject
import utils.PlatformUtils

@Composable
fun AppStateBanners(
  newUpdate: Boolean,
  noInternet: Boolean,
  cantReach: Boolean,
  modifier: Modifier = Modifier,
) {
  val density = LocalDensity.current
  val mainInsets = WindowInsets.statusBars
  val mainInsetsTop = mainInsets.getTop(density)
  SubcomposeLayout(modifier = modifier) { constraints ->
    val noInternetPlaceable = subcompose(0) {
      AnimatedVisibility(
        visible = noInternet || cantReach,
        enter = expandVertically(),
        exit = shrinkVertically(),
      ) {
        NoInternet(
          modifier = Modifier.windowInsetsPadding(mainInsets),
          noInternet = noInternet,
        )
      }
    }.fastMap { it.measure(constraints) }
    val noInternetHeight = noInternetPlaceable.fastMaxBy { it.height }?.height ?: 0

    val newUpdatePlaceable = subcompose(1) {
      AnimatedVisibility(
        visible = newUpdate,
        enter = expandVertically(),
        exit = shrinkVertically(),
      ) {
        val top = (mainInsetsTop - noInternetHeight).coerceAtLeast(0)
        NewUpdateBanner(
          modifier = Modifier.windowInsetsPadding(WindowInsets(top = top)),
        )
      }
    }.fastMap { it.measure(constraints) }
    val newUpdateHeight = newUpdatePlaceable.fastMaxBy { it.height }?.height ?: 0

    layout(constraints.maxWidth, noInternetHeight + newUpdateHeight) {
      noInternetPlaceable.fastForEach {
        it.place(0, 0)
      }
      newUpdatePlaceable.fastForEach {
        it.place(0, noInternetHeight)
      }
    }
  }
}

@Composable
private fun NewUpdateBanner(modifier: Modifier = Modifier) {
  // I don't care about injecting platform utils into this small composable.
  val platformUtils = koinInject<PlatformUtils>()
  Text(
    text = stringResource(MR.strings.new_update_available),
    modifier = Modifier
      .background(MaterialTheme.colorScheme.tertiary)
      .fillMaxWidth()
      .clickable { platformUtils.openURI(platformUtils.getString(MR.strings.repository_url) + "/releases/latest") }
      .padding(4.dp)
      .then(modifier),
    color = MaterialTheme.colorScheme.onTertiary,
    textAlign = TextAlign.Center,
    style = MaterialTheme.typography.labelMedium,
  )
}

@Composable
private fun NoInternet(
  noInternet: Boolean,
  modifier: Modifier = Modifier,
) {
  Text(
    stringResource(
      if (noInternet) MR.strings.connectivity_no_internet else MR.strings.connectivity_no_progres,
    ),
    color = MaterialTheme.colorScheme.onErrorContainer,
    textAlign = TextAlign.Center,
    style = MaterialTheme.typography.labelMedium,
    modifier = Modifier
      .background(MaterialTheme.colorScheme.errorContainer)
      .fillMaxWidth()
      .padding(4.dp)
      .then(modifier),
  )
}
