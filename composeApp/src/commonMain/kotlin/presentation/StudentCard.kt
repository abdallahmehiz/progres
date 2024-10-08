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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import mehiz.abdallah.progres.domain.models.AccommodationModel
import mehiz.abdallah.progres.domain.models.StudentCardModel
import org.jetbrains.compose.resources.painterResource
import progres.composeapp.generated.resources.Res
import progres.composeapp.generated.resources.card_student_empty
import progres.composeapp.generated.resources.card_student_empty_dz
import progres.composeapp.generated.resources.onou
import kotlin.math.abs

val scaledFontSize: @Composable (TextUnit) -> TextUnit = {
  it * (ScreenWidthPixels() / 1080.0)
}

@Composable
fun StudentCardDialog(
  card: StudentCardModel,
  accommodationState: AccommodationModel?,
  onDismissRequest: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val rotationState = remember { Animatable(0f) }
  val scope = rememberCoroutineScope()
  var showBackSide by remember { mutableStateOf(false) }
  Dialog(
    onDismissRequest = onDismissRequest,
    properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = null,
          onClick = onDismissRequest,
        )
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
      shape = RoundedCornerShape(20.dp),
    ),
  ) {
    val (cardBackground, cardContent) = createRefs()
    Image(
      painter = painterResource(
        if (type == CardType.EMPTY) Res.drawable.card_student_empty else Res.drawable.card_student_empty_dz,
      ),
      null,
      contentScale = ContentScale.FillBounds,
      modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp)).constrainAs(cardBackground) {},
    )
    if (type != CardType.EMPTY) {
      Column(
        modifier = Modifier.aspectRatio(1.8f).fillMaxSize().padding(horizontal = 16.dp).constrainAs(cardContent) {
          start.linkTo(cardBackground.start)
          top.linkTo(cardBackground.top)
          bottom.linkTo(cardBackground.bottom)
          end.linkTo(cardBackground.end)
        },
        verticalArrangement = Arrangement.SpaceBetween,
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

@Composable
fun CardHeader(
  card: StudentCardModel,
  accommodationState: AccommodationModel?,
  type: CardType,
  modifier: Modifier = Modifier,
) {
  Row(modifier) {
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
      verticalArrangement = Arrangement.SpaceAround,
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
        Text(
          text = card.individualPlaceOfBirthArabic,
          color = Color.DarkGray,
          fontSize = scaledFontSize(2.em),
          lineHeight = 1.sp,
          fontWeight = FontWeight.ExtraBold,
        )
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
    AsyncImage(
      model = card.photo,
      contentDescription = null,
      contentScale = ContentScale.Fit,
      alignment = Alignment.CenterEnd,
      modifier = Modifier.aspectRatio(1 / 1.1f).weight(1f),
    )
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
