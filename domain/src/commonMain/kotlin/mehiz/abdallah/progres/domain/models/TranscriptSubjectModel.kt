package mehiz.abdallah.progres.domain.models

import mehiz.abdallah.progres.api.dto.TranscriptSubjectsDto
import mehiz.abdallah.progres.data.db.TranscriptSubjectTable

data class TranscriptSubjectModel(
  val id: Long,
  val ueId: Long,
  val sessionId: Long,
  val subjectId: Long,
  val subjectStringLatin: String,
  val subjectStringArabic: String,
  val subjectCode: String,
  val coefficient: Double,
  val credit: Double,
  val creditObtained: Double,
  val average: Double?
)

fun TranscriptSubjectsDto.toTable(): TranscriptSubjectTable {
  return TranscriptSubjectTable(
    id = id.toLong(),
    ueId = bilanUeId.toLong(),
    sessionId = bilanSessionId.toLong(),
    subjectId = rattachementMcId.toLong(),
    subjectStringLatin = mcLibelleFr,
    subjectStringArabic = mcLibelleAr,
    subjectCode = mcCode,
    coefficient = coefficient,
    credit = credit,
    creditObtained = creditObtenu,
    average = moyenneGenerale
  )
}

fun TranscriptSubjectTable.toModel(): TranscriptSubjectModel {
  return TranscriptSubjectModel(
    id = id,
    ueId = ueId,
    sessionId = sessionId,
    subjectId = subjectId,
    subjectStringLatin = subjectStringLatin,
    subjectStringArabic = subjectStringArabic,
    subjectCode = subjectCode,
    coefficient = coefficient,
    credit = credit,
    creditObtained = creditObtained,
    average = average
  )
}
