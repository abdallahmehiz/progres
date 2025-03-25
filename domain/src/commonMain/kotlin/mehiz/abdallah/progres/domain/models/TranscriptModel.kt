package mehiz.abdallah.progres.domain.models

import mehiz.abdallah.progres.api.dto.TranscriptDto
import mehiz.abdallah.progres.data.db.TranscriptTable

data class TranscriptModel(
  val period: AcademicPeriodModel,
  val average: Double?,
  val averageSn: Double?,
  val credit: Double?,
  val creditObtained: Double?,
  val levelStringLatin: String,
  val levelStringArabic: String,
  val coefficient: Double?,
  val ues: List<TranscriptUeModel>
)

fun TranscriptDto.toTable(
  yearPeriodCode: String,
): TranscriptTable {
  return TranscriptTable(
    id = bilanUes[0].bilanSessionId,
    periodStringLatin = periodeLibelleFr,
    periodStringArabic = periodeLibelleAr,
    average = moyenne,
    averageSn = moyenneSn,
    credit = credit,
    creditObtained = creditObtenu,
    levelStringLatin = niveauLibelleLongLt,
    levelStringArabic = niveauLibelleLongAr,
    coefficient = coefficient,
    yearPeriodCode = yearPeriodCode
  )
}

fun TranscriptTable.toModel(
  period: AcademicPeriodModel,
  ues: List<TranscriptUeModel>
): TranscriptModel {
  return TranscriptModel(
    average = average,
    averageSn = averageSn,
    credit = credit,
    creditObtained = creditObtained,
    levelStringLatin = levelStringLatin,
    levelStringArabic = levelStringArabic,
    coefficient = coefficient,
    ues = ues,
    period = period
  )
}
