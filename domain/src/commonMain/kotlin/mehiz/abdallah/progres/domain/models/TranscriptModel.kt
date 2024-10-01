package mehiz.abdallah.progres.domain.models

import mehiz.abdallah.progres.api.dto.TranscriptDto
import mehiz.abdallah.progres.data.db.TranscriptTable

data class TranscriptModel(
  val id: Long,
  val type: Long,
  val period: AcademicPeriodModel,
  val average: Double?,
  val averageSn: Double?,
  val credit: Double?,
  val creditObtained: Double?,
  val creditAcquired: Double?,
  val isAnnual: Boolean,
  val cycleStringLatin: String,
  val levelCode: String,
  val levelRank: Long,
  val levelStringLatin: String,
  val levelStringArabic: String,
  val coefficient: Double?,
  val ues: List<TranscriptUeModel>
)

fun TranscriptDto.toTable(
  yearPeriodCode: String
): TranscriptTable {
  return TranscriptTable(
    id = id,
    type = type,
    periodId = periodeId,
    periodStringLatin = periodeLibelleFr,
    periodStringArabic = periodeLibelleAr,
    average = moyenne,
    averageSn = moyenneSn,
    credit = credit,
    creditObtained = creditObtenu,
    creditAcquired = creditAcquis,
    isAnnual = if (annuel) 1 else 0,
    cycleStringLatin = cycleLibelleLongLt,
    levelCode = niveauCode,
    levelRank = niveauRang,
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
    id = id,
    type = type,
    average = average,
    averageSn = averageSn,
    credit = credit,
    creditObtained = creditObtained,
    creditAcquired = creditAcquired,
    isAnnual = isAnnual != 0L,
    cycleStringLatin = cycleStringLatin,
    levelCode = levelCode,
    levelRank = levelRank,
    levelStringLatin = levelStringLatin,
    levelStringArabic = levelStringArabic,
    coefficient = coefficient,
    ues = ues,
    period = period
  )
}
