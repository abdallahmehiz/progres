package ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.MotionPhotosPause
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Settings
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
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import dev.materii.pullrefresh.PullRefreshLayout
import dev.materii.pullrefresh.rememberPullRefreshState
import kotlinx.coroutines.launch
import mehiz.abdallah.progres.domain.models.AccommodationStateModel
import mehiz.abdallah.progres.domain.models.BacInfoModel
import mehiz.abdallah.progres.domain.models.StudentCardModel
import mehiz.abdallah.progres.i18n.MR
import presentation.CardType
import presentation.MaterialPullRefreshIndicator
import presentation.StudentCard
import ui.home.ccgradesscreen.CCGradesScreen
import ui.home.enrollments.EnrollmentsScreen
import ui.home.examgrades.ExamGradesScreen
import ui.home.examsschedule.ExamsScheduleScreen
import ui.home.groups.GroupsScreen
import ui.home.subjects.SubjectsScreen
import ui.home.subjectsschedule.SubjectsScheduleScreen
import ui.home.transcriptsscreen.TranscriptScreen
import ui.preferences.PreferencesScreen
import kotlin.math.abs

object HomeScreen : Screen {

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val screenModel = koinScreenModel<HomeScreenModel>()
    val isRefreshing by screenModel.isRefreshing.collectAsState()
    val ptrState = rememberPullRefreshState(isRefreshing, { screenModel.refresh() })
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text(stringResource(MR.strings.home_title)) },
          actions = {
            IconButton(onClick = { navigator.push(PreferencesScreen) }) {
              Icon(Icons.Rounded.Settings, null)
            }
          },
          windowInsets = WindowInsets(0.dp)
        )
      },
    ) { paddingValues ->
      val data by screenModel.data.collectAsState()
      PullRefreshLayout(
        ptrState,
        modifier = Modifier
          .padding(paddingValues)
          .fillMaxSize(),
        indicator = { MaterialPullRefreshIndicator(ptrState) },
      ) {
        data.DisplayResult(
          onLoading = { HomeScreenContent(null, null, null) },
          onSuccess = { HomeScreenContent(it.studentCard, it.accommodationStateModel, it.bacInfo) },
          onError = { HomeScreenContent(null, null, null) },
        )
      }
    }
  }

  @Composable
  fun HomeScreenContent(
    card: StudentCardModel?,
    accommodationState: AccommodationStateModel?,
    bacInfo: BacInfoModel?,
    modifier: Modifier = Modifier,
  ) {
    ConstraintLayout(
      modifier = modifier
        .padding(horizontal = 16.dp)
        .fillMaxSize(),
    ) {
      val (profileCard, bacInfoDropDown, screensGrid) = createRefs()
      var isStudentCardShown by remember { mutableStateOf(false) }
      var isStudentBacInfoShown by remember { mutableStateOf(false) }
      var isStudentPhotoShown by remember { mutableStateOf(false) }
      val showStudentCard: () -> Unit = { isStudentCardShown = true }
      ProfileTile(
        card,
        onClick = { isStudentBacInfoShown = !isStudentBacInfoShown },
        isExpanded = isStudentBacInfoShown,
        onCardClick = if (card == null) null else showStudentCard,
        onPhotoClick = { isStudentPhotoShown = true },
        modifier = Modifier.constrainAs(profileCard) {
          top.linkTo(parent.top)
          end.linkTo(parent.end)
          start.linkTo(parent.start)
        }.fillMaxWidth(),
      )
      if (isStudentCardShown) {
        Dialog(
          onDismissRequest = { isStudentCardShown = false },
          properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
          Box(
            Modifier.clickable(
              interactionSource = remember { MutableInteractionSource() },
              indication = null,
            ) {
              isStudentCardShown = false
            },
          )
          if (card == null) return@Dialog
          StudentCardDialogContent(card = card, accommodationState = accommodationState)
        }
      }
      AnimatedVisibility(
        isStudentBacInfoShown,
        enter = expandVertically { -it },
        exit = shrinkVertically { -it },
        modifier = Modifier
          .constrainAs(bacInfoDropDown) {
            top.linkTo(profileCard.bottom, 8.dp)
            start.linkTo(profileCard.start)
            end.linkTo(profileCard.end)
          }
          .zIndex(200f),
      ) {
        if (bacInfo == null) {
          isStudentBacInfoShown = false
          return@AnimatedVisibility
        }
        BacInfoCard(bacInfo)
      }
      if (isStudentPhotoShown) {
        StudentPhotoAlert(photo = card?.photo!!, { isStudentPhotoShown = false })
      }
      ScreensGrid(
        card == null,
        modifier = Modifier.constrainAs(screensGrid) {
          top.linkTo(profileCard.bottom, 16.dp)
          end.linkTo(profileCard.end)
          start.linkTo(profileCard.start)
        },
      )
    }
  }

  @Composable
  fun ProfileTile(
    card: StudentCardModel?,
    onClick: () -> Unit,
    isExpanded: Boolean,
    onCardClick: (() -> Unit)?,
    onPhotoClick: () -> Unit,
    modifier: Modifier = Modifier,
  ) {
    val clipBottom by animateDpAsState(if (isExpanded) 8.dp else 32.dp)
    Row(
      modifier = modifier
        .animateContentSize()
        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp, bottomStart = clipBottom, bottomEnd = clipBottom))
        .background(MaterialTheme.colorScheme.primaryContainer)
        .clickable(onClick = onClick)
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      AsyncImage(
        card?.photo,
        null,
        modifier = Modifier
          .width(54.dp)
          .aspectRatio(1f)
          .clip(CircleShape)
          .clickable(onClick = onPhotoClick),
        contentScale = ContentScale.FillWidth,
      )
      Column(Modifier.weight(1f)) {
        Text(
          stringResource(
            MR.strings.student_name,
            card?.individualLastNameLatin ?: "",
            card?.individualFirstNameLatin ?: "",
          ),
        )
        Text(card?.academicYearString ?: "")
      }
      IconButton(onClick = onCardClick ?: {}, enabled = onCardClick != null) {
        Icon(Icons.Filled.Badge, null)
      }
    }
  }

  @Composable
  fun StudentCardDialogContent(
    card: StudentCardModel,
    accommodationState: AccommodationStateModel?,
    modifier: Modifier = Modifier,
  ) {
    val rotationState = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var showBackSide by remember { mutableStateOf(false) }

    Box(
      modifier = modifier.fillMaxSize()
        .pointerInput(Unit) {
          detectHorizontalDragGestures(
            onDragEnd = {
              scope.launch {
                rotationState.animateTo(
                  if (abs(rotationState.value) !in 90f..270f) {
                    if (rotationState.value < 90) 0f else 360f
                  } else {
                    if (rotationState.value < 90) -180f else 180f
                  },
                )
                showBackSide = abs(rotationState.targetValue) in 90f..270f
              }
            },
          ) { _, dragAmount ->
            scope.launch { rotationState.animateTo(rotationState.value + dragAmount) }
            scope.launch {
              showBackSide = abs(rotationState.targetValue) in 90f..270f
              if (abs(rotationState.targetValue) >= 360f) {
                rotationState.snapTo(0f)
              } else if (rotationState.targetValue <= -180f) {
                rotationState.snapTo(180f)
              }
              showBackSide = abs(rotationState.value) in 90f..270f
            }
          }
        },
      contentAlignment = Alignment.Center,
    ) {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        StudentCard(
          card = card,
          accommodationState = accommodationState,
          type = when {
            accommodationState != null -> CardType.ACCOMMODATION
            card.isTransportPaid -> CardType.TRANSPORT
            else -> CardType.EMPTY
          },
          modifier = Modifier.graphicsLayer {
            rotationY = -rotationState.value
            scaleX = 1.5f
            scaleY = 1.5f
            rotationZ = 90f
            rotationX = 180f
            cameraDistance = 18 * density
          },
        )

        StudentCard(
          card = card,
          accommodationState = null,
          type = CardType.FRONT,
          modifier = Modifier.graphicsLayer {
            alpha = if (showBackSide) 0f else 1f
            rotationY = rotationState.value
            scaleX = 1.5f
            scaleY = 1.5f
            rotationZ = -90f
            cameraDistance = 18 * density
          },
        )
      }
    }
  }

  data class SubScreen(
    val icon: ImageVector,
    val title: StringResource,
    val destination: Screen? = null,
    val enabled: Boolean = true,
  )

  private val screens = listOf(
    SubScreen(Icons.Rounded.MotionPhotosPause, MR.strings.home_discharge, enabled = false),
    SubScreen(Icons.Rounded.CalendarViewMonth, MR.strings.home_time_table, SubjectsScheduleScreen),
    SubScreen(Icons.Rounded.House, MR.strings.home_accommodation, enabled = false),
    SubScreen(Icons.Rounded.People, MR.strings.home_group, GroupsScreen),
    SubScreen(Icons.Rounded.AccountTree, MR.strings.home_subjects, SubjectsScreen),
    SubScreen(Icons.Rounded.CalendarMonth, MR.strings.home_exams_schedule, ExamsScheduleScreen),
    SubScreen(Icons.Rounded.EditNote, MR.strings.home_exams_results, ExamGradesScreen),
    SubScreen(Icons.Rounded.DoneAll, MR.strings.home_continuous_eval, CCGradesScreen),
    SubScreen(Icons.Rounded.FolderCopy, MR.strings.home_academic_transcripts, TranscriptScreen),
    SubScreen(Icons.Rounded.Calculate, MR.strings.home_debts, enabled = false),
    SubScreen(Icons.AutoMirrored.Rounded.Note, MR.strings.home_academic_vacations, enabled = false),
    SubScreen(Icons.Rounded.Inventory2, MR.strings.home_enrollments, EnrollmentsScreen),
    // SubScreen(Icons.AutoMirrored.Rounded.FactCheck, MR.strings.home_bac_results, BacInfoScreen),
    SubScreen(Icons.Rounded.Restaurant, MR.strings.home_restaurant, enabled = false),
    SubScreen(Icons.Rounded.MoreHoriz, MR.strings.home_more_services, enabled = false),
  )

  @OptIn(ExperimentalFoundationApi::class)
  @Composable
  fun SubScreenTile(
    icon: ImageVector,
    titleResource: StringResource,
    destination: Screen?,
    enabled: Boolean,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
  ) {
    val navigator = LocalNavigator.currentOrThrow
    Row(
      modifier.clip(RoundedCornerShape(16.dp)).clickable(enabled = enabled && !isLoading) {
        destination?.let { navigator.push(it) }
      }.background(MaterialTheme.colorScheme.surfaceContainerHigh).alpha(if (enabled) 1f else .5f).padding(16.dp),
      horizontalArrangement = if (isLoading) Arrangement.Center else Arrangement.spacedBy(4.dp),
    ) {
      if (isLoading) {
        CircularProgressIndicator(
          modifier = Modifier.size(20.dp),
          strokeWidth = 4.dp,
        )
      } else {
        Icon(
          icon,
          null,
          tint = MaterialTheme.colorScheme.primary,
        )
        Text(
          stringResource(titleResource),
          maxLines = 1,
          modifier = Modifier.basicMarquee(),
        )
      }
    }
  }

  @Composable
  fun ScreensGrid(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
  ) {
    LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = modifier,
    ) {
      items(screens) {
        SubScreenTile(
          it.icon,
          it.title,
          it.destination,
          it.enabled,
          isLoading,
        )
      }
    }
  }

  // Un-reusable lol
  @OptIn(ExperimentalFoundationApi::class)
  @Composable
  fun BacInfoCard(
    model: BacInfoModel,
    modifier: Modifier = Modifier,
  ) {
    Column(
      modifier
        .clickable(
          onClick = {},
          interactionSource = remember { MutableInteractionSource() },
          indication = null,
        ),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Column(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(MaterialTheme.colorScheme.tertiaryContainer)
          .padding(16.dp),
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text(
            model.seriesString,
            maxLines = 1,
            modifier = Modifier
              .basicMarquee(),
          )
          Text(model.bacYear.toString())
        }
        Row(
          modifier = Modifier
            .fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
        }
        Row(
          modifier = Modifier
            .fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text(stringResource(MR.strings.generic_average))
          Text(model.grade.toString())
        }
      }
      LazyColumn(
        modifier = Modifier
          .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
          .clip(RoundedCornerShape(bottomStart = 50f, bottomEnd = 50f))
          .background(MaterialTheme.colorScheme.secondaryContainer)
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        items(
          model.grades,
        ) {
          Column {
            Row(
              modifier = Modifier
                .fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Text(
                it.subjectName,
                maxLines = 1,
                modifier = Modifier
                  .basicMarquee(),
              )
              Text(stringResource(MR.strings.grade, it.grade, 20))
            }
            if (it != model.grades.last()) {
              HorizontalDivider(
                color = MaterialTheme.colorScheme.onSecondaryContainer,
              )
            }
          }
        }
      }
    }
  }

  @Composable
  fun StudentPhotoAlert(
    photo: ByteArray?,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
  ) {
    Dialog(
      onDismissRequest,
      properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
      AsyncImage(
        photo,
        null,
        contentScale = ContentScale.Fit,
        modifier = modifier.fillMaxWidth(),
      )
    }
  }
}
