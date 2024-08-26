package presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil3.compose.AsyncImage
import dev.icerock.moko.resources.compose.painterResource
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents.Companion.Format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import mehiz.abdallah.progres.domain.models.StudentCardModel
import mehiz.abdallah.progres.i18n.MR

@Composable
fun StudentCard(
  card: StudentCardModel,
  type: CardType,
  modifier: Modifier = Modifier,
) {
  ConstraintLayout(
    modifier = modifier
      .fillMaxWidth()
      .shadow(
        elevation = 8.dp,
        shape = RoundedCornerShape(20.dp),
      ),
  ) {
    val (cardBackground, cardContent) = createRefs()
    Image(
      painter = painterResource(
        if (type == CardType.FRONT) {
          MR.images.card_student_empty_dz
        } else {
          MR.images.card_student_empty
        }
      ),
      null,
      contentScale = ContentScale.FillBounds,
      modifier = Modifier
        .clip(RoundedCornerShape(20.dp))
        .fillMaxWidth()
        .constrainAs(cardBackground) {},
    )
    if (type != CardType.EMPTY) {
      Column(
        modifier = Modifier
          .aspectRatio(1.8f)
          .fillMaxSize()
          .padding(horizontal = 16.dp)
          .constrainAs(cardContent) {
            start.linkTo(cardBackground.start)
            top.linkTo(cardBackground.top)
            bottom.linkTo(cardBackground.bottom)
            end.linkTo(cardBackground.end)
          },
        verticalArrangement = Arrangement.SpaceBetween,
      ) {
        Column {
          CardHeader(card = card, type = type)
          CardInformationRow(card = card, type = type)
        }
        CardFooter(card = card)
      }
    }
  }
}

@Composable
fun CardHeader(
  card: StudentCardModel,
  type: CardType,
  modifier: Modifier = Modifier,
) {
  Row(modifier) {
    Image(
      painter = painterResource(MR.images.onou_logo),
      contentDescription = null,
      alpha = if (type != CardType.FRONT) 0f else 1f,
      contentScale = ContentScale.FillWidth,
      modifier = Modifier.weight(1f),
    )
    Column(
      modifier = Modifier.weight(3.6f),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Top,
    ) {
      Text(
        text = "الجمهورية الجزائرية الديموقراطية الشعبية",
        color = Color.Black,
        fontSize = 2.4.em,
        lineHeight = 1.sp,
      )
      Text(
        text = "وزارة التربية الوطنية و البحث العلمي",
        color = Color.Black,
        fontSize = 2.4.em,
        lineHeight = 1.sp,
      )
      Text(
        text = card.establishmentStringArabic,
        color = Color.Black,
        fontSize = 2.4.em,
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
        fontSize = 4.em,
        fontWeight = FontWeight.ExtraBold,
        lineHeight = 1.sp,
      )
    }
    AsyncImage(
      model = if (type == CardType.FRONT) card.establishmentLogo else MR.images.onou_logo,
      null,
      contentScale = ContentScale.FillWidth,
      modifier = Modifier
        .weight(1f)
        .aspectRatio(1f),
    )
  }
}

@OptIn(FormatStringsInDatetimeFormats::class)
@Composable
fun CardInformationRow(
  card: StudentCardModel,
  type: CardType,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(
      modifier = Modifier.weight(1f),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceAround,
    ) {
      Image(
        painter = rememberQrCodePainter(data = "https://progres.mesrs.dz/check/${card.registrationNumber}"),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier.fillMaxWidth(0.6f),
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
        fontSize = 2.em,
        lineHeight = 1.sp,
      )
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          text = card.individualLastNameLatin,
          color = Color.DarkGray,
          fontSize = 2.em,
          lineHeight = 1.sp,
          fontWeight = FontWeight.ExtraBold,
        )
        Text(
          text = card.individualLastNameArabic,
          color = Color.DarkGray,
          fontSize = 2.em,
          lineHeight = 1.sp,
          fontWeight = FontWeight.ExtraBold,
        )
      }
      Text(
        text = "الاسم",
        color = Color.DarkGray,
        fontSize = 2.em,
        lineHeight = 1.sp,
      )
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          text = card.individualFirstNameLatin,
          color = Color.DarkGray,
          fontSize = 2.em,
          lineHeight = 1.sp,
          fontWeight = FontWeight.ExtraBold,
        )
        Text(
          text = card.individualFirstNameArabic,
          color = Color.DarkGray,
          fontSize = 2.em,
          lineHeight = 1.sp,
          fontWeight = FontWeight.ExtraBold,
        )
      }
      Text(
        text = "تاريخ و مكان الميلاد",
        color = Color.DarkGray,
        fontSize = 2.em,
        lineHeight = 1.sp,
      )
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          text = card.individualDateOfBirth.format(LocalDateTime.Format { byUnicodePattern("dd/MM/yyyy") }),
          color = Color.DarkGray,
          fontSize = 2.em,
          lineHeight = 1.sp,
          fontWeight = FontWeight.ExtraBold,
        )
        Text(
          text = card.individualPlaceOfBirthArabic,
          color = Color.DarkGray,
          fontSize = 2.em,
          lineHeight = 1.sp,
          fontWeight = FontWeight.ExtraBold,
        )
      }
      Text(
        text = "الميدان",
        color = Color.DarkGray,
        fontSize = 2.em,
        lineHeight = 1.sp,
      )
      Text(
        text = card.ofDomainStringArabic,
        color = Color.DarkGray,
        fontSize = 2.em,
        lineHeight = 1.sp,
        fontWeight = FontWeight.ExtraBold,
      )
      Text(
        text = "الفرع",
        color = Color.DarkGray,
        fontSize = 2.em,
        lineHeight = 1.sp,
      )
      Text(
        text = card.ofFieldStringArabic,
        color = Color.DarkGray,
        fontSize = 2.em,
        lineHeight = 1.sp,
        fontWeight = FontWeight.ExtraBold,
      )
    }
    AsyncImage(
      model = card.photo,
      contentDescription = null,
      contentScale = ContentScale.Fit,
      alignment = Alignment.CenterEnd,
      modifier = Modifier
        .aspectRatio(1 / 1.1f)
        .weight(1f),
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
    modifier = modifier
      .fillMaxWidth()
      .padding(top = 8.dp),
  ) {
    Text(
      text = card.registrationNumber,
      color = Color.DarkGray,
      fontSize = 2.em,
      lineHeight = 1.sp,
      fontWeight = FontWeight.ExtraBold,
    )
    Text(
      text = "السنة الجامعية: ${card.academicYearString}",
      color = Color.DarkGray,
      fontSize = 2.em,
      lineHeight = 1.sp,
      fontWeight = FontWeight.ExtraBold,
    )
  }
}

enum class CardType {
  FRONT,
  TRANSPORT,
  ACCOMMODATION,
  EMPTY,
}
