package ui.preferences

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CollectionsBookmark
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import compose.icons.SimpleIcons
import compose.icons.simpleicons.Github
import dev.icerock.moko.resources.compose.stringResource
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import mehiz.abdallah.progres.BuildKonfig.BuildKonfig
import mehiz.abdallah.progres.i18n.MR
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import progres.composeapp.generated.resources.Res
import progres.composeapp.generated.resources.abdallah_mehiz
import progres.composeapp.generated.resources.app_icon
import progres.composeapp.generated.resources.idir_yacine
import progres.composeapp.generated.resources.touhami_ahmed
import ui.crash.collectDeviceInfo
import utils.PlatformUtils

object AboutScreen : Screen {
  override val key = uniqueScreenKey

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val platformUtils = koinInject<PlatformUtils>()
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text(text = stringResource(MR.strings.about_title)) },
          navigationIcon = {
            IconButton(onClick = navigator::pop) {
              Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
            }
          },
        )
      },
    ) { paddingValues ->
      Column(
        modifier = Modifier.padding(paddingValues).verticalScroll(rememberScrollState()),
      ) {
        Column(
          Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
          // .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp)),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Image(
            painter = painterResource(Res.drawable.app_icon),
            null,
            modifier = Modifier.size(92.dp),
          )
          Text(
            text = stringResource(MR.strings.app_name),
            style = MaterialTheme.typography.headlineSmall,
          )
          Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Column {
              Text(
                stringResource(MR.strings.about_by),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp),
              )
              DeveloperIcon(
                devName = "Mehiz Abdallah",
                Res.drawable.abdallah_mehiz,
                socialLink = "https://github.com/abdallahmehiz",
                onClick = platformUtils::openURI,
              )
            }
            Column {
              Text(
                stringResource(MR.strings.about_special_thanks),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp),
              )
              DeveloperIcon(
                devName = "IDIR Yacine",
                Res.drawable.idir_yacine,
                socialLink = "https://github.com/IDIRYACINE",
                onClick = platformUtils::openURI,
              )
              DeveloperIcon(
                devName = "Ahmed Touhami",
                Res.drawable.touhami_ahmed,
                socialLink = "https://github.com/mustafachyi",
                onClick = platformUtils::openURI,
              )
            }
          }
        }
        HorizontalDivider()
        ProvidePreferenceLocals {
          Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Preference(
              title = { Text(stringResource(MR.strings.about_app_version)) },
              icon = { Icon(Icons.Rounded.Update, null) },
              summary = { Text(BuildKonfig.VERSION_NAME) },
              onClick = { platformUtils.copyTextToClipboard(text = collectDeviceInfo()) },
            )
            Preference(
              title = { Text(stringResource(MR.strings.about_oss_libraries)) },
              icon = { Icon(Icons.Rounded.CollectionsBookmark, null) },
              onClick = { navigator.push(LibrariesScreen) },
            )
            Preference(
              title = { Text(stringResource(MR.strings.about_privacy_policy)) },
              icon = { Icon(Icons.Rounded.Policy, null) },
              onClick = { platformUtils.openURI("https://abdallahmehiz.github.io/progres/privacy.html") },
            )
          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
          ) {
            IconButton(onClick = { platformUtils.openURI(platformUtils.getString(MR.strings.repository_url)) }) {
              Icon(SimpleIcons.Github, stringResource(MR.strings.repository_url), modifier = Modifier)
            }
          }
        }
      }
    }
  }

  @Composable
  fun DeveloperIcon(
    devName: String,
    image: DrawableResource,
    socialLink: String,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
  ) {
    Row(
      modifier = modifier.height(IntrinsicSize.Min).clickable(onClick = { onClick(socialLink) }).fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Image(
        painter = painterResource(image),
        contentDescription = devName,
        modifier = Modifier.size(36.dp).clip(CircleShape),
        contentScale = ContentScale.Fit,
        alignment = Alignment.Center,
        alpha = 1f,
      )
      Column(verticalArrangement = Arrangement.Center) {
        Text(
          devName,
          maxLines = 2,
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyLarge,
        )
        Text(
          socialLink,
          maxLines = 2,
          textAlign = TextAlign.Center,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          style = MaterialTheme.typography.bodyMedium,
        )
      }
    }
  }

  object LibrariesScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
      val navigator = LocalNavigator.currentOrThrow
      Scaffold(
        topBar = {
          TopAppBar(
            title = {
              Text(text = stringResource(MR.strings.about_oss_libraries))
            },
            navigationIcon = {
              IconButton(onClick = navigator::pop) {
                Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
              }
            },
          )
        },
      ) { paddingValues ->
        var libraries: Libs? by remember { mutableStateOf(null) }
        LaunchedEffect(Unit) {
          libraries = Libs.Builder().withJson(Res.readBytes("files/aboutlibraries.json").decodeToString()).build()
        }
        if (libraries == null) {
          Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
          ) { CircularProgressIndicator() }
        } else {
          LibrariesContainer(
            libraries = libraries,
            modifier = Modifier.padding(paddingValues),
          )
        }
      }
    }
  }
}
