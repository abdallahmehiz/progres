package ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import coil3.compose.AsyncImage
import org.kodein.di.compose.localDI
import org.kodein.di.instance

object HomeScreen : Screen {

  @Composable
  override fun Content() {
    val viewModel by localDI().instance<HomeScreenViewModel>()
    Column(
      modifier = Modifier
        .padding(horizontal = 16.dp)
    ) {
      val photo by viewModel.studentPhoto.collectAsState()
      ProfileTile(
        photo,
        modifier = Modifier
          .fillMaxWidth()
      )
    }
  }

  @Composable
  fun ProfileTile(
    photo: ByteArray?,
    modifier: Modifier = Modifier,
  ) {
    Row(
      modifier = modifier
        .clip(RoundedCornerShape(16.dp))
        .background(MaterialTheme.colorScheme.primaryContainer)
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      AsyncImage(
        photo,
        null,
        modifier = Modifier
          .width(76.dp)
          .aspectRatio(1f)
          .clip(CircleShape),
        contentScale = ContentScale.FillBounds
      )
      Column {
        Text("Student Name") // placeholder,
        Text("Academic Year") // placeholder,
      }
    }
  }
}
