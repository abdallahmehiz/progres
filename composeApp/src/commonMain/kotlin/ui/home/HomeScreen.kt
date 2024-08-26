package ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.core.screen.Screen
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import mehiz.abdallah.progres.domain.models.StudentCardModel
import org.kodein.di.compose.localDI
import org.kodein.di.instance
import presentation.CardType
import presentation.StudentCard
import kotlin.math.abs

object HomeScreen : Screen {

  @Composable
  override fun Content() {
    val viewModel by localDI().instance<HomeScreenViewModel>()
    Column(
      modifier = Modifier
        .padding(horizontal = 16.dp)
    ) {
      val photo by viewModel.studentPhoto.collectAsState()
      var isStudentCardShown by remember { mutableStateOf(false) }
      val card by viewModel.studentCard.collectAsState()
      val showStudentCard: () -> Unit = { isStudentCardShown = true }
      ProfileTile(
        photo,
        onCardClick = if (card == null) null else showStudentCard,
        modifier = Modifier
          .fillMaxWidth()
      )
      if (isStudentCardShown) {
        Dialog(
          onDismissRequest = { isStudentCardShown = false },
          properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
          if (card == null) return@Dialog
          StudentCardDialogContent(card = card)
        }
      }
    }
  }

  @Composable
  fun ProfileTile(
    photo: ByteArray?,
    onCardClick: (() -> Unit)?,
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
      Spacer(Modifier.weight(1f))
      IconButton(onClick = onCardClick ?: {}, enabled = onCardClick != null) {
        Icon(Icons.Filled.Badge, null)
      }
    }
  }

  @Composable
  fun StudentCardDialogContent(
    card: StudentCardModel?,
    modifier: Modifier = Modifier,
  ) {
    val rotationState = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var showBackCard by remember { mutableStateOf(false) }
    Box(
      modifier = modifier
        .fillMaxSize()
        .pointerInput(key1 = null) {
          detectHorizontalDragGestures(
            onDragEnd = {
              scope.launch {
                if (abs(rotationState.value.toInt()) !in 90..270) {
                  rotationState.animateTo(
                    if (rotationState.value < 90) 0f else 360f,
                    animationSpec = tween(
                      durationMillis = 600,
                      easing = FastOutSlowInEasing,
                    ),
                  )
                } else {
                  rotationState.animateTo(
                    if (rotationState.value.toInt() < 90) -180f else 180f,
                    animationSpec = tween(
                      durationMillis = 600,
                      easing = FastOutSlowInEasing,
                    ),
                  )
                }
              }
            },
          ) { change, dragAmount ->
            scope.launch { rotationState.animateTo(rotationState.value + dragAmount) }
            showBackCard = abs(rotationState.value.toInt()) in 90..270
            if (abs(rotationState.value) >= 360f) {
              scope.launch {
                rotationState.snapTo(0f)
              }
            } else if (rotationState.value <= -180f) {
              scope.launch {
                rotationState.snapTo(180f)
              }
            }
          }
        },
      contentAlignment = Alignment.Center,
    ) {
      if (card != null) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
          StudentCard(
            card = card,
            type = CardType.FRONT,
            modifier = Modifier.graphicsLayer {
              alpha = if (showBackCard) 0f else 1f
              rotationY = rotationState.value
              scaleX = 1.5f
              scaleY = 1.5f
              rotationZ = -90f
              cameraDistance = 18 * density
            },
          )
          StudentCard(
            card = card,
            modifier = Modifier.graphicsLayer {
              alpha = if (showBackCard) 1f else 0f
              rotationY = -rotationState.value
              scaleX = 1.5f
              scaleY = 1.5f
              rotationZ = 90f
              rotationX = 180f
              cameraDistance = 18 * density
            },
            type = when {
              card.isTransportPaid -> CardType.TRANSPORT
              // TODO card.isAccommodationPaid == true -> CardType.ACCOMMODATION
              else -> CardType.EMPTY
            },
          )
        }
      }
    }
  }
}
