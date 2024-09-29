package mehiz.abdallah.progres.domain.models

import mehiz.abdallah.progres.api.dto.TranscriptUEDto
import mehiz.abdallah.progres.data.db.TranscriptUETable

data class TranscriptUeModel(
  val id: Long,
  val sessionId: Long,
  val rapartitionUEId: Long,
  val ueStringLatin: String,
  val ueCode: String,
  val ueType: String,
  val average: Double,
  val coefficient: Double,
  val credit: Double,
  val creditObtained: Double,
  val creditAcquired: Double,
  val ueNatureLatin: String,
  val ueNatureArabic: String,
  val ueNatureCode: String,
  val uePassingGrade: Double,
  val ueAcquired: Boolean,
  val subjects: List<TranscriptSubjectModel>
)

fun TranscriptUEDto.toTable(): TranscriptUETable {
  return TranscriptUETable(
    id = id.toLong(),
    sessionId = bilanSessionId.toLong(),
    rapartitionUEId = repartitionUeId.toLong(),
    ueStringLatin = ueLibelleFr,
    ueCode = ueCode,
    ueType = ueType,
    average = moyenne,
    coefficient = coefficient,
    credit = credit,
    creditObtained = creditObtenu,
    creditAcquired = creditAcquis,
    ueNatureLatin = ueNatureLcFr,
    ueNatureArabic = ueNatureLcAr,
    ueNatureCode = ueNatureCode,
    uePassingGrade = ueNoteObtention,
    ueAcquired = if (ueAcquis) 1.0 else 0.0,
  )
}

fun TranscriptUETable.toModel(
  subjects: List<TranscriptSubjectModel>
): TranscriptUeModel {
  return TranscriptUeModel(
    id = id,
    sessionId = sessionId,
    rapartitionUEId = rapartitionUEId,
    ueStringLatin = ueStringLatin,
    ueCode = ueCode,
    ueType = ueType,
    average = average,
    coefficient = coefficient,
    credit = credit,
    creditObtained = creditObtained,
    creditAcquired = creditAcquired,
    ueNatureLatin = ueNatureLatin,
    ueNatureArabic = ueNatureArabic,
    ueNatureCode = ueNatureCode,
    uePassingGrade = uePassingGrade,
    ueAcquired = ueAcquired != 0.0,
    subjects = subjects
  )
}
