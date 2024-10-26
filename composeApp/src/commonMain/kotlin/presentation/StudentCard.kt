package presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import coil3.compose.AsyncImage
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.Napier
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import io.github.alexzhirkevich.qrose.toByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import mehiz.abdallah.progres.core.TAG
import mehiz.abdallah.progres.domain.models.AccommodationModel
import mehiz.abdallah.progres.domain.models.StudentCardModel
import mehiz.abdallah.progres.i18n.MR
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import presentation.capturable.capturable
import presentation.capturable.rememberCaptureController
import progres.composeapp.generated.resources.Res
import progres.composeapp.generated.resources.card_student_empty
import progres.composeapp.generated.resources.card_student_empty_dz
import progres.composeapp.generated.resources.onou
import utils.Platform
import utils.PlatformUtils
import kotlin.math.abs

val scaledFontSize: @Composable (TextUnit) -> TextUnit = {
  val platformUtils = koinInject<PlatformUtils>()
  it * (ScreenWidthPixels() / 1080.0 * if (platformUtils.platform == Platform.Ios) 0.3f else 1f)
}

@OptIn(ExperimentalComposeUiApi::class)
@Suppress("CyclomaticComplexMethod")
@Composable
fun StudentCardDialog(
  card: StudentCardModel,
  accommodationState: AccommodationModel?,
  onDismissRequest: () -> Unit,
  canSave: Boolean,
  canShare: Boolean,
  onSave: (ByteArray) -> Unit,
  onShare: (ByteArray) -> Unit,
  modifier: Modifier = Modifier,
) {
  val cardFrontCaptureController = rememberCaptureController()
  val cardBackCaptureController = rememberCaptureController()
  val rotationState = remember { Animatable(0f) }
  val scope = rememberCoroutineScope()
  var showBackSide by remember { mutableStateOf(false) }
  Dialog(
    onDismissRequest = onDismissRequest,
    properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    // Find a solution to not use scaffold here
    Scaffold(
      modifier = modifier
        .clickable(
          onClick = onDismissRequest,
          interactionSource = remember { MutableInteractionSource() },
          indication = null,
        ),
      containerColor = Color.Transparent,
      bottomBar = {
        Row(modifier = Modifier.fillMaxWidth()) {
          CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
            if (canSave) {
              BoxButton(
                onClick = {
                  scope.launch(Dispatchers.IO) {
                    val bitmap =
                      (if (showBackSide) cardBackCaptureController else cardFrontCaptureController).captureAsync()
                    try {
                      onSave(bitmap.await().toByteArray())
                    } catch (e: Exception) {
                      Napier.e(tag = TAG, throwable = e) { e.stackTraceToString() }
                    }
                  }
                },
                modifier = Modifier
                  .weight(1f),
              ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  Icon(Icons.Rounded.Save, null)
                  Text(stringResource(MR.strings.generic_save))
                }
              }
            }
            if (canShare) {
              BoxButton(
                onClick = {
                  scope.launch(Dispatchers.IO) {
                    val bitmap =
                      (if (showBackSide) cardBackCaptureController else cardFrontCaptureController).captureAsync()
                    try {
                      onShare(bitmap.await().toByteArray())
                    } catch (e: Exception) {
                      Napier.e(tag = TAG, throwable = e) { e.stackTraceToString() }
                    }
                  }
                },
                modifier = Modifier
                  .weight(1f),
              ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  Icon(Icons.Rounded.Share, null)
                  Text(stringResource(MR.strings.generic_share))
                }
              }
            }
          }
        }
      },
    ) { paddingValues ->
      Column(
        modifier = Modifier
          .padding(paddingValues)
          .fillMaxSize()
          .windowInsetsPadding(WindowInsets.systemBars)
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onDismissRequest,
          )
          .pointerInput(Unit) {
            var originalValue = rotationState.value
            var startingX = 0f
            detectHorizontalDragGestures(
              onDragStart = {
                startingX = it.x
                originalValue = rotationState.value
              },
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
            ) { change, _ ->
              scope.launch { rotationState.animateTo(originalValue + ((change.position.x - startingX) * .5f)) }
              scope.launch {
                showBackSide = abs(rotationState.targetValue) in 90f..270f
                if (abs(rotationState.targetValue) >= 360f) {
                  rotationState.snapTo(0f)
                  originalValue = rotationState.value
                  startingX = change.position.x
                } else if (rotationState.targetValue <= -180f) {
                  rotationState.snapTo(180f)
                  originalValue = rotationState.value
                  startingX = change.position.x
                }
                showBackSide = abs(rotationState.value) in 90f..270f
              }
            }
          },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
      ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
          Box(
            modifier = Modifier
              .scale(1.5f)
              .rotate(-90f)
              .clickable(
                onClick = {},
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
              ),
          ) {
            StudentCard(
              card = card,
              accommodationState = null,
              type = CardType.FRONT,
              modifier = Modifier.graphicsLayer {
                alpha = if (showBackSide) 0f else 1f
                rotationX = -rotationState.value
                cameraDistance = 18 * density
              }
                .capturable(cardFrontCaptureController),
            )
            StudentCard(
              card = card,
              accommodationState = accommodationState,
              type = when {
                accommodationState != null -> CardType.ACCOMMODATION
                card.isTransportPaid -> CardType.TRANSPORT
                else -> CardType.EMPTY
              },
              modifier = Modifier.graphicsLayer {
                alpha = if (showBackSide) 1f else 0f
                rotationX = -rotationState.value
                rotationZ = 180f
                rotationY = 180f
                cameraDistance = 18 * density
              }
                .capturable(cardBackCaptureController),
            )
          }
        }
      }
    }
  }
}

@Composable
fun StudentCard(
  card: StudentCardModel,
  accommodationState: AccommodationModel?,
  type: CardType,
  modifier: Modifier = Modifier,
) {
  ConstraintLayout(
    modifier = modifier.fillMaxWidth().aspectRatio(1.7f).shadow(
      elevation = 8.dp,
      shape = RoundedCornerShape(10),
    ),
  ) {
    val (cardBackground, cardContent) = createRefs()
    Image(
      painter = painterResource(
        if (type == CardType.EMPTY) Res.drawable.card_student_empty else Res.drawable.card_student_empty_dz,
      ),
      null,
      contentScale = ContentScale.FillBounds,
      modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10)).constrainAs(cardBackground) {},
    )
    if (type != CardType.EMPTY) {
      Column(
        modifier = Modifier
          .aspectRatio(1.7f)
          .constrainAs(cardContent) {
            start.linkTo(cardBackground.start)
            top.linkTo(cardBackground.top)
            bottom.linkTo(cardBackground.bottom)
            end.linkTo(cardBackground.end)
          },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth(.9f)
            .fillMaxHeight(.95f),
        ) {
          Column {
            CardHeader(card = card, accommodationState = accommodationState, type = type)
            CardInformationRow(card = card, accommodationState = accommodationState, type = type)
          }
          Spacer(Modifier.weight(1f))
          CardFooter(card = card)
        }
      }
    }
  }
}

@Composable
fun CardHeader(
  card: StudentCardModel,
  accommodationState: AccommodationModel?,
  type: CardType,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Box(
      Modifier.weight(1f),
    ) {
      if (type == CardType.FRONT) {
        Image(
          painter = painterResource(Res.drawable.onou),
          contentDescription = null,
          alpha = if (type != CardType.FRONT) 0f else 1f,
          contentScale = ContentScale.FillWidth,
        )
      }
    }
    Column(
      modifier = Modifier.weight(3.4f),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Top,
    ) {
      Text(
        text = "الجمهورية الجزائرية الديموقراطية الشعبية",
        color = Color.Black,
        fontSize = scaledFontSize(2.4.em),
        lineHeight = 1.sp,
      )
      Text(
        text = "وزارة التربية الوطنية و البحث العلمي",
        color = Color.Black,
        fontSize = scaledFontSize(2.4.em),
        lineHeight = 1.sp,
      )
      Text(
        text = if (type == CardType.ACCOMMODATION && accommodationState != null) {
          accommodationState.providerStringArabic
        } else {
          card.establishment.nameArabic
        },
        color = Color.Black,
        fontSize = scaledFontSize(2.4.em),
        lineHeight = 1.sp,
      )
      Text(
        text = when (type) {
          CardType.FRONT -> "بطاقة الطالب"
          CardType.TRANSPORT -> "بطاقة النقل"
          CardType.ACCOMMODATION -> "بطاقة الإقامة"
          else -> ""
        },
        color = Color.Black,
        fontSize = scaledFontSize(4.em),
        fontWeight = FontWeight.ExtraBold,
        lineHeight = 1.sp,
      )
    }
    Box(
      Modifier.weight(1f),
      contentAlignment = Alignment.Center,
    ) {
      if (type == CardType.FRONT) {
        AsyncImage(model = card.establishment.photo, null)
      } else {
        Image(painter = painterResource(Res.drawable.onou), null)
      }
    }
  }
}

@Suppress("CyclomaticComplexMethod")
@OptIn(FormatStringsInDatetimeFormats::class)
@Composable
fun CardInformationRow(
  card: StudentCardModel,
  accommodationState: AccommodationModel?,
  type: CardType,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(
      modifier = Modifier.weight(.8f),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      Image(
        painter = rememberQrCodePainter(
          data = when (type) {
            CardType.TRANSPORT -> "https://progres.mesrs.dz/api/infos/checkTransport/${card.id}"
            CardType.ACCOMMODATION -> "https://progres.mesrs.dz/api/infos/checkHebergement/${card.id}"
            else -> "https://progres.mesrs.dz/api/infos/checkInscription/${card.id}"
          },
        ),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        alignment = Alignment.CenterStart,
        modifier = Modifier.fillMaxWidth(0.7f),
      )
      if (type == CardType.FRONT && card.isTransportPaid) {
        Icon(
          Icons.Filled.DirectionsBus,
          null,
          Modifier.fillMaxSize(0.5f),
          tint = Color(0xFFFFB148),
        )
      }
    }
    Column(
      modifier = Modifier.weight(2f),
      horizontalAlignment = Alignment.End,
    ) {
      Text(
        text = "اللقب",
        color = Color.DarkGray,
        fontSize = scaledFontSize(2.em),
        lineHeight = 1.sp,
      )
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          text = card.individualLastNameLatin,
          color = Color.DarkGray,
          fontSize = scaledFontSize(2.em),
          lineHeight = 1.sp,
          fontWeight = FontWeight.ExtraBold,
        )
        Text(
          text = card.individualLastNameArabic,
          color = Color.DarkGray,
          fontSize = scaledFontSize(2.em),
          lineHeight = 1.sp,
          fontWeight = FontWeight.ExtraBold,
        )
      }
      Text(
        text = "الاسم",
        color = Color.DarkGray,
        fontSize = scaledFontSize(2.em),
        lineHeight = 1.sp,
      )
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          text = card.individualFirstNameLatin,
          color = Color.DarkGray,
          fontSize = scaledFontSize(2.em),
          lineHeight = 1.sp,
          fontWeight = FontWeight.ExtraBold,
        )
        Text(
          text = card.individualFirstNameArabic,
          color = Color.DarkGray,
          fontSize = scaledFontSize(2.em),
          lineHeight = 1.sp,
          fontWeight = FontWeight.ExtraBold,
        )
      }
      Text(
        text = "تاريخ و مكان الميلاد",
        color = Color.DarkGray,
        fontSize = scaledFontSize(2.em),
        lineHeight = 1.sp,
      )
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          text = card.individualDateOfBirth.format(LocalDateTime.Format { byUnicodePattern("dd/MM/yyyy") }),
          color = Color.DarkGray,
          fontSize = scaledFontSize(2.em),
          lineHeight = 1.sp,
          fontWeight = FontWeight.ExtraBold,
        )
        if (card.individualPlaceOfBirthArabic != null) {
          Text(
            text = card.individualPlaceOfBirthArabic!!,
            color = Color.DarkGray,
            fontSize = scaledFontSize(2.em),
            lineHeight = 1.sp,
            fontWeight = FontWeight.ExtraBold,
          )
        }
      }
      if (type == CardType.ACCOMMODATION && accommodationState != null) {
        Text(
          text = "الإقامة",
          color = Color.DarkGray,
          fontSize = scaledFontSize(2.em),
          lineHeight = 1.sp,
        )
        Text(
          text = accommodationState.residenceStringArabic,
          color = Color.DarkGray,
          fontSize = scaledFontSize(2.em),
          lineHeight = 1.sp,
          fontWeight = FontWeight.ExtraBold,
        )
        if (accommodationState.assignedPavillion != null) {
          Text(
            text = "الجناح و الغرفة",
            color = Color.DarkGray,
            fontSize = scaledFontSize(2.em),
            lineHeight = 1.sp,
          )
          Text(
            text = accommodationState.assignedPavillion!!,
            color = Color.DarkGray,
            fontSize = scaledFontSize(2.em),
            lineHeight = 1.sp,
            fontWeight = FontWeight.ExtraBold,
          )
        }
      } else {
        Text(
          text = "الميدان",
          color = Color.DarkGray,
          fontSize = scaledFontSize(2.em),
          lineHeight = 1.sp,
        )
        Text(
          text = card.ofDomainStringArabic,
          color = Color.DarkGray,
          fontSize = scaledFontSize(2.em),
          lineHeight = 1.sp,
          fontWeight = FontWeight.ExtraBold,
        )
        if (card.ofFieldStringArabic != null || card.ofSpecialtyStringArabic != null) {
          Text(
            text = "الفرع",
            color = Color.DarkGray,
            fontSize = scaledFontSize(2.em),
            lineHeight = 1.sp,
          )
          Text(
            text = card.ofFieldStringArabic ?: card.ofSpecialtyStringArabic ?: "",
            color = Color.DarkGray,
            fontSize = scaledFontSize(2.em),
            lineHeight = 1.sp,
            fontWeight = FontWeight.ExtraBold,
          )
        }
      }
    }
    if (card.photo == null) {
      Icon(
        Icons.Filled.AccountBox,
        null,
        tint = Color.Black,
        modifier = Modifier.weight(1f).fillMaxWidth(.8f).aspectRatio(1 / 1.1f),
      )
    } else {
      AsyncImage(
        model = card.photo,
        contentDescription = null,
        contentScale = ContentScale.Fit,
        alignment = Alignment.CenterEnd,
        modifier = Modifier.aspectRatio(1 / 1.1f).weight(1f),
      )
    }
  }
}

@Composable
fun CardFooter(
  card: StudentCardModel,
  modifier: Modifier = Modifier,
) {
  Row(
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier.fillMaxWidth().padding(top = 8.dp),
  ) {
    Text(
      text = card.registrationNumber,
      color = Color.DarkGray,
      fontSize = scaledFontSize(2.em),
      lineHeight = 1.sp,
      fontWeight = FontWeight.ExtraBold,
    )
    Text(
      text = "السنة الجامعية: ${card.academicYearString}",
      color = Color.DarkGray,
      fontSize = scaledFontSize(2.em),
      lineHeight = 1.sp,
      fontWeight = FontWeight.ExtraBold,
    )
  }
}

enum class CardType {
  FRONT, TRANSPORT, ACCOMMODATION, EMPTY,
}
