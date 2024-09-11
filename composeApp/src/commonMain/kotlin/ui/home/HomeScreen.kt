package ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.FactCheck
import androidx.compose.material.icons.automirrored.rounded.Note
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.rounded.AccountTree
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CalendarViewMonth
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.FolderCopy
import androidx.compose.material.icons.rounded.House
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.MotionPhotosPause
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import mehiz.abdallah.progres.domain.models.StudentCardModel
import mehiz.abdallah.progres.i18n.MR
import org.kodein.di.compose.localDI
import org.kodein.di.instance
import presentation.CardType
import presentation.StudentCard
import presentation.preferences.PreferenceFooter
import ui.home.enrollments.EnrollmentsScreen
import ui.home.examgrades.ExamGradesScreen
import ui.home.examsschedule.ExamsScheduleScreen
import ui.preferences.PreferencesScreen
import kotlin.math.abs

object HomeScreen : Screen {

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val viewModel by localDI().instance<HomeScreenViewModel>()
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text(stringResource(MR.strings.home_title)) },
          actions = {
            IconButton(onClick = { navigator.push(PreferencesScreen) }) {
              Icon(Icons.Rounded.Settings, null)
            }
          },
        )
      },
    ) { paddingValues ->
      Column(
        modifier = Modifier.padding(paddingValues).padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        var isStudentCardShown by remember { mutableStateOf(false) }
        val card by viewModel.studentCard.collectAsState()
        val showStudentCard: () -> Unit = { isStudentCardShown = true }
        ProfileTile(
          card,
          onCardClick = if (card == null) null else showStudentCard,
          modifier = Modifier.fillMaxWidth(),
        )
        if (isStudentCardShown) {
          Dialog(
            onDismissRequest = { isStudentCardShown = false },
            properties = DialogProperties(usePlatformDefaultWidth = false),
          ) {
            if (card == null) return@Dialog
            StudentCardDialogContent(card = card)
          }
        }
        ScreensGrid()
        Spacer(Modifier.weight(1f))
        PreferenceFooter(stringResource(MR.strings.alert_unofficial_app_note))
        Spacer(Modifier.height(16.dp))
      }
    }
  }

  @Composable
  fun ProfileTile(
    card: StudentCardModel?,
    onCardClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
  ) {
    Row(
      modifier = modifier.clip(RoundedCornerShape(50)).background(MaterialTheme.colorScheme.primaryContainer)
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      AsyncImage(
        card?.photo,
        null,
        modifier = Modifier.width(54.dp).aspectRatio(1f).clip(CircleShape),
        contentScale = ContentScale.FillWidth,
      )
      Column {
        Text(
          stringResource(
            MR.strings.student_name,
            card?.individualLastNameLatin ?: "",
            card?.individualFirstNameLatin ?: "",
          ),
        )
        Text(card?.academicYearString ?: "")
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
      modifier = modifier.fillMaxSize().pointerInput(key1 = null) {
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

  data class SubScreen(
    val icon: ImageVector,
    val title: StringResource,
    val destination: Screen?,
    val enabled: Boolean = true,
  )

  private val screens = listOf(
    SubScreen(Icons.Rounded.MotionPhotosPause, MR.strings.home_discharge, null, false),
    SubScreen(Icons.Rounded.CalendarViewMonth, MR.strings.home_time_table, null, false),
    SubScreen(Icons.Rounded.House, MR.strings.home_accommodation, null, false),
    SubScreen(Icons.Rounded.People, MR.strings.home_group, null, false),
    SubScreen(Icons.Rounded.AccountTree, MR.strings.home_subjects, null, false),
    SubScreen(Icons.Rounded.CalendarMonth, MR.strings.home_exams_schedule, ExamsScheduleScreen),
    SubScreen(Icons.Rounded.EditNote, MR.strings.home_exams_results, ExamGradesScreen),
    SubScreen(Icons.Rounded.DoneAll, MR.strings.home_continuous_eval, null, false),
    SubScreen(Icons.Rounded.FolderCopy, MR.strings.home_academic_transcripts, null, false),
    SubScreen(Icons.Rounded.Calculate, MR.strings.home_debts, null, false),
    SubScreen(Icons.AutoMirrored.Rounded.Note, MR.strings.home_academic_vacations, null, false),
    SubScreen(Icons.Rounded.Inventory2, MR.strings.home_enrollments, EnrollmentsScreen),
    SubScreen(Icons.AutoMirrored.Rounded.FactCheck, MR.strings.home_bac_results, null, false),
  )

  @OptIn(ExperimentalFoundationApi::class)
  @Composable
  fun SubScreenTile(
    subScreen: SubScreen,
    modifier: Modifier = Modifier,
  ) {
    val navigator = LocalNavigator.currentOrThrow
    Row(
      modifier.clip(RoundedCornerShape(16.dp)).clickable(enabled = subScreen.enabled) {
        subScreen.destination?.let { navigator.push(it) }
      }
        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        .alpha(if (subScreen.enabled) 1f else .5f)
        .padding(16.dp),
      horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Icon(
        subScreen.icon,
        null,
        tint = MaterialTheme.colorScheme.primary,
      )
      Text(
        stringResource(subScreen.title),
        maxLines = 1,
        modifier = Modifier.basicMarquee(),
      )
    }
  }

  @Composable
  fun ScreensGrid(
    modifier: Modifier = Modifier,
  ) {
    LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = modifier,
    ) {
      items(screens) { SubScreenTile(it, modifier = Modifier.fillMaxWidth()) }
    }
  }
}
