package mehiz.abdallah.progres.domain.models

import mehiz.abdallah.progres.api.dto.TranscriptUEDto
import mehiz.abdallah.progres.data.db.TranscriptUETable

data class TranscriptUeModel(
  val id: Long,
  val sessionId: Long,
  val ueStringLatin: String,
  val ueStringArabic: String,
  val average: Double,
  val credit: Double,
  val creditAcquired: Double,
  val ueNatureLatin: String,
  val ueNatureArabic: String,
  val subjects: List<TranscriptSubjectModel>
)

fun TranscriptUEDto.toTable(): TranscriptUETable {
  return TranscriptUETable(
    id = bilanMcs[0].bilanUeId,
    sessionId = bilanSessionId,
    ueStringLatin = ueLibelleFr,
    ueStringArabic = ueLibelleAr,
    average = moyenne,
    credit = credit,
    creditAcquired = creditAcquis,
    ueNatureLatin = ueNatureLcFr,
    ueNatureArabic = ueNatureLcAr,
  )
}

fun TranscriptUETable.toModel(
  subjects: List<TranscriptSubjectModel>
): TranscriptUeModel {
  return TranscriptUeModel(
    id = id,
    sessionId = sessionId,
    ueStringLatin = ueStringLatin,
    ueStringArabic = ueStringArabic,
    average = average,
    creditAcquired = creditAcquired,
    credit = credit,
    ueNatureLatin = ueNatureLatin,
    ueNatureArabic = ueNatureArabic,
    subjects = subjects
  )
}
