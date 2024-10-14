package ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Title
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import coil3.compose.AsyncImage
import com.dokar.sonner.Toast
import com.dokar.sonner.ToasterState
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.placeholder
import com.eygraber.compose.placeholder.shimmer
import com.svenjacobs.reveal.Reveal
import com.svenjacobs.reveal.RevealCanvasState
import com.svenjacobs.reveal.RevealOverlayArrangement
import com.svenjacobs.reveal.RevealOverlayScope
import com.svenjacobs.reveal.rememberRevealState
import com.svenjacobs.reveal.shapes.balloon.Arrow
import com.svenjacobs.reveal.shapes.balloon.Balloon
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import dev.materii.pullrefresh.PullRefreshLayout
import dev.materii.pullrefresh.rememberPullRefreshState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime
import mehiz.abdallah.progres.domain.AccountUseCase
import mehiz.abdallah.progres.domain.models.BacInfoModel
import mehiz.abdallah.progres.domain.models.StudentCardModel
import mehiz.abdallah.progres.domain.models.SubjectScheduleModel
import mehiz.abdallah.progres.i18n.MR
import org.koin.compose.koinInject
import preferences.BasePreferences
import presentation.ErrorScreenContent
import presentation.MaterialPullRefreshIndicator
import presentation.Pulsation
import presentation.PulsationType
import presentation.SaveAndShareButtons
import presentation.StudentCardDialog
import presentation.errorToast
import ui.home.subjectsschedule.ScheduleDataNode
import ui.home.subjectsschedule.subjectBackgroundColor
import ui.home.subjectsschedule.subjectTextColor
import ui.onboarding.LoginScreen
import ui.preferences.PreferencesScreen
import utils.FirebaseUtils
import utils.PlatformUtils
import utils.isNetworkError

object HomeScreen : Screen {

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val platformUtils = koinInject<PlatformUtils>()
    val preferences = koinInject<BasePreferences>()
    val accountUseCase = koinInject<AccountUseCase>()
    val screenModel = koinScreenModel<HomeScreenModel>()
    var isRefreshing by remember { mutableStateOf(false) }
    val toasterState = koinInject<ToasterState>()
    val ptrState = rememberPullRefreshState(
      refreshing = isRefreshing,
      onRefresh = {
        isRefreshing = true
        screenModel.screenModelScope.launch(Dispatchers.IO) {
          try {
            screenModel.refresh()
          } catch (e: Exception) {
            if (!e.isNetworkError) FirebaseUtils.reportException(e)
            toasterState.show(errorToast(e.stackTraceToString()))
          }
          isRefreshing = false
        }
      },
    )
    LaunchedEffect(Unit) {
      screenModel.screenModelScope.launch(Dispatchers.IO) {
        try {
          screenModel.tryRefreshToken()
        } catch (e: Exception) {
          // prompt to manually relogin when the password is incorrect (changed outside)
          if (e.message?.contains("incorrecte") == true) {
            preferences.isLoggedIn.set(false)
            toasterState.show(Toast(platformUtils.getString(MR.strings.toast_manual_re_login_necessary)))
            accountUseCase.logout()
            navigator.replaceAll(LoginScreen)
          }
        }
      }
    }
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text(stringResource(MR.strings.home_title)) },
          actions = {
            IconButton(onClick = { navigator.push(PreferencesScreen) }) {
              Icon(Icons.Rounded.Settings, null)
            }
          },
          windowInsets = WindowInsets(0.dp),
        )
      },
    ) { paddingValues ->
      val data by screenModel.data.collectAsState()
      val nextSchedule by screenModel.nextSchedule.collectAsState()
      PullRefreshLayout(
        ptrState,
        modifier = Modifier
          .padding(paddingValues)
          .fillMaxSize(),
        indicator = { MaterialPullRefreshIndicator(ptrState) },
      ) {
        data.DisplayResult(
          onLoading = { HomeScreenContent(null, null) },
          onSuccess = { HomeScreenContent(it, nextSchedule) },
          onError = { ErrorScreenContent(it) },
        )
      }
    }
  }

  @OptIn(InternalVoyagerApi::class)
  @Composable
  fun HomeScreenContent(
    homeScreenUIData: HomeScreenUIData?,
    nextSchedule: SubjectScheduleModel?,
    modifier: Modifier = Modifier,
  ) {
    val scope = rememberCoroutineScope()
    ConstraintLayout(
      modifier = modifier
        .padding(horizontal = 16.dp)
        .fillMaxSize(),
    ) {
      val (profileCard, bacInfoDropDown, screensGrid) = createRefs()
      val schedulePulse = createRef()
      var isStudentCardShown by remember { mutableStateOf(false) }
      var isStudentBacInfoShown by remember { mutableStateOf(false) }
      var isStudentPhotoShown by remember { mutableStateOf(false) }
      val showStudentCard: () -> Unit = { isStudentCardShown = true }
      ProfileTile(
        homeScreenUIData?.studentCard,
        onClick = { isStudentBacInfoShown = !isStudentBacInfoShown },
        isExpanded = isStudentBacInfoShown,
        onCardClick = if (homeScreenUIData?.studentCard == null) null else showStudentCard,
        onPhotoClick = { isStudentPhotoShown = true },
        modifier = Modifier.constrainAs(profileCard) {
          top.linkTo(parent.top)
          end.linkTo(parent.end)
          start.linkTo(parent.start)
        }.fillMaxWidth(),
      )
      if (homeScreenUIData?.studentCard != null && isStudentCardShown) {
        StudentCardDialog(
          card = homeScreenUIData.studentCard,
          accommodationState = homeScreenUIData.accommodationStateModel,
          onDismissRequest = { isStudentCardShown = false },
        )
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
        if (homeScreenUIData?.bacInfo == null) {
          isStudentBacInfoShown = false
          return@AnimatedVisibility
        }
        Box(
          Modifier.fillMaxSize().clickable(
            onClick = { isStudentBacInfoShown = false },
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
          ),
        )
        BackHandler(true) {
          isStudentBacInfoShown = false
        }
        BacInfoCard(homeScreenUIData.bacInfo)
      }
      if (isStudentPhotoShown && homeScreenUIData != null) {
        StudentPhotoDialog(
          photo = homeScreenUIData.studentCard.photo,
          onDismissRequest = { isStudentPhotoShown = false },
          studentId = homeScreenUIData.studentCard.serialNumber,
        )
      }
      val revealCanvasState = koinInject<RevealCanvasState>()
      val revealState = rememberRevealState()
      Reveal(
        revealCanvasState = revealCanvasState,
        revealState = revealState,
        onRevealableClick = { scope.launch { revealState.reveal(nextSchedule!!.id) } },
        onOverlayClick = { scope.launch { revealState.hide() } },
        overlayContent = { NextSubjectOverlayContent(model = nextSchedule!!) },
        modifier = Modifier.constrainAs(schedulePulse) {
          top.linkTo(profileCard.bottom, 8.dp)
          end.linkTo(profileCard.end)
          start.linkTo(profileCard.start)
        },
      ) {
        NextSchedulePulse(
          nextSchedule,
          modifier = Modifier
            .then(if (nextSchedule != null) Modifier.revealable(nextSchedule.id, revealState) else Modifier)
            .clickable(
              onClick = { scope.launch { revealState.reveal(nextSchedule!!.id) } },
              interactionSource = remember { MutableInteractionSource() },
              indication = null,
            ),
        )
      }
      ScreensGrid(
        homeScreenUIData?.studentCard == null,
        modifier = Modifier.constrainAs(screensGrid) {
          top.linkTo(schedulePulse.bottom, 8.dp)
          end.linkTo(profileCard.end)
          start.linkTo(profileCard.start)
        }.fillMaxSize(),
      )
    }
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Composable
  fun NextSchedulePulse(
    schedule: SubjectScheduleModel?,
    modifier: Modifier = Modifier,
  ) {
    Row(
      modifier
        .fillMaxWidth()
        .animateContentSize(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      if (schedule == null) {
        Box(Modifier)
      } else {
        Pulsation(
          enabled = true,
          type = PulsationType.Linear(
            duration = 2000,
            pulseRange = 1f..2.5f,
          ),
          modifier = Modifier.padding(8.dp),
        ) {
          Box(Modifier.size(12.dp).clip(CircleShape).background(Color.Red))
        }
        Text(
          schedule.ap,
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(subjectBackgroundColor(schedule.ap))
            .padding(2.dp),
          color = subjectTextColor(schedule.ap),
        )
        Text(
          schedule.subjectStringLatin,
          modifier = Modifier.weight(1f).basicMarquee(),
          maxLines = 1,
        )
        Text("-")
        Text(schedule.hourlyRangeStart?.formatHHmm() ?: "")
      }
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
      if (card?.photo == null) {
        Icon(
          Icons.Rounded.AccountCircle,
          null,
          modifier = Modifier.size(54.dp),
        )
      } else {
        AsyncImage(
          card.photo,
          null,
          modifier = Modifier
            .width(54.dp)
            .aspectRatio(1f)
            .clip(CircleShape)
            .clickable(onClick = onPhotoClick, enabled = card.photo != null),
          contentScale = ContentScale.FillWidth,
        )
      }
      Column(
        Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(2.dp),
      ) {
        Text(
          stringResource(
            MR.strings.student_name,
            card?.individualLastNameLatin ?: "",
            card?.individualFirstNameLatin ?: "",
          ),
          style = MaterialTheme.typography.bodyLarge,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.textPlaceholder(card == null, .7f),
        )
        Text(
          card?.academicYearString ?: "",
          style = MaterialTheme.typography.bodyMedium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.textPlaceholder(card == null, .4f),
        )
      }
      FilledIconButton(onClick = onCardClick ?: {}, enabled = onCardClick != null) {
        Icon(Icons.Rounded.Badge, null)
      }
    }
  }

  @Composable
  fun SubScreenTile(
    icon: ImageVector,
    titleResource: StringResource,
    destination: Screen?,
    enabled: Boolean,
    isBeta: Boolean,
    modifier: Modifier = Modifier,
  ) {
    val navigator = LocalNavigator.currentOrThrow
    Box(modifier) {
      Row(
        Modifier
          .clip(RoundedCornerShape(16.dp))
          .clickable(enabled = enabled) {
            destination?.let { navigator.push(it) }
          }.background(MaterialTheme.colorScheme.surfaceContainerHigh)
          .fillMaxWidth().alpha(if (enabled) 1f else .5f).padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          icon,
          null,
          tint = MaterialTheme.colorScheme.primary,
        )
        Text(
          stringResource(titleResource),
          maxLines = 1,
          style = MaterialTheme.typography.bodyMedium,
          overflow = TextOverflow.Ellipsis,
        )
      }
      if (isBeta) {
        Text(
          stringResource(MR.strings.generic_beta),
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onPrimaryContainer,
          modifier = Modifier
            .align(Alignment.TopEnd)
            .clip(RoundedCornerShape(topStart = 0.dp, topEnd = 16.dp, bottomEnd = 0.dp, bottomStart = 16.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 8.dp, vertical = 2.dp),
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
          enabled = it.enabled && !isLoading,
          isBeta = it.isBeta,
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
        if (model.seriesString != null) {
          Row(
            modifier = Modifier
              .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Text(
              model.seriesString!!,
              maxLines = 1,
              modifier = Modifier
                .weight(1f)
                .basicMarquee(),
            )
            Text(
              stringResource(MR.strings.generic_int, model.bacYear),
              maxLines = 1
            )
          }
        }
        Row(
          modifier = Modifier
            .fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text(
            stringResource(MR.strings.generic_average),
            maxLines = 1,
            modifier = Modifier.basicMarquee()
          )
          Text(
            stringResource(MR.strings.generic_float, model.grade),
            maxLines = 1
          )
        }
      }
      if (model.grades.isNotEmpty()) {
        LazyColumn(
          modifier = Modifier
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .clip(RoundedCornerShape(bottomStart = 50f, bottomEnd = 50f))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          items(model.grades) {
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
                Text(stringResource(MR.strings.grade_int, it.grade, 20))
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
  }

  @Composable
  fun StudentPhotoDialog(
    photo: ByteArray?,
    onDismissRequest: () -> Unit,
    studentId: String,
    modifier: Modifier = Modifier,
  ) {
    Dialog(
      onDismissRequest,
      properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
      Column(modifier) {
        AsyncImage(
          photo,
          null,
          contentScale = ContentScale.Fit,
          modifier = Modifier.fillMaxWidth(),
        )
        if (photo != null) {
          SaveAndShareButtons(photo, "progres-photo-$studentId.jpg", Modifier.heightIn(max = 48.dp))
        }
      }
    }
  }

  @Composable
  fun RevealOverlayScope.NextSubjectOverlayContent(
    model: SubjectScheduleModel,
    modifier: Modifier = Modifier,
  ) {
    Balloon(
      arrow = Arrow.top(),
      backgroundColor = MaterialTheme.colorScheme.surfaceBright,
      modifier = modifier.align(RevealOverlayArrangement.Bottom),
    ) {
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
        Column {
          ScheduleDataNode(
            Icons.Rounded.Info,
            stringResource(MR.strings.subjects_schedule_type),
            model.ap,
          )
          ScheduleDataNode(
            Icons.Rounded.Title,
            stringResource(MR.strings.subjects_schedule_subject),
            model.subjectStringLatin,
          )
          ScheduleDataNode(
            Icons.Rounded.Groups,
            stringResource(MR.strings.groups_group),
            model.groupString,
          )
          if (model.teacherFirstNameLatin != null && model.teacherLastNameLatin != null) {
            ScheduleDataNode(
              Icons.Rounded.School,
              stringResource(MR.strings.subjects_schedule_teacher),
              model.teacherFirstNameLatin + ' ' + model.teacherLastNameLatin,
            )
          }
          if (model.locationDesignation != null) {
            ScheduleDataNode(
              Icons.Rounded.LocationOn,
              stringResource(MR.strings.subjects_schedule_location),
              model.locationDesignation!!,
            )
          }
          if (model.hourlyRangeStringLatin != null) {
            ScheduleDataNode(
              Icons.Rounded.Schedule,
              stringResource(MR.strings.subjects_schedule_hourly_range),
              model.hourlyRangeStringLatin!!,
            )
          }
        }
      }
    }
  }
}

inline fun LocalTime.formatHHmm(): String {
  val hour = hour.toString().padStart(2, '0')
  val minute = minute.toString().padStart(2, '0')
  return "$hour:$minute"
}

@Composable
@Suppress("ModifierComposable")
fun Modifier.textPlaceholder(
  visible: Boolean,
  textSize: Float,
): Modifier {
  return this then Modifier
    .clip(RoundedCornerShape(8.dp))
    .placeholder(
      visible,
      color = MaterialTheme.colorScheme.onPrimary,
      highlight = PlaceholderHighlight.shimmer(Color.LightGray),
    ).then(
      if (visible) {
        Modifier.fillMaxWidth(textSize)
      } else {
        Modifier
      },
    )
}
