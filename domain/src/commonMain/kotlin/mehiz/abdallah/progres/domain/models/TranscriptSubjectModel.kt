package mehiz.abdallah.progres.domain.models

import mehiz.abdallah.progres.api.dto.TranscriptSubjectsDto
import mehiz.abdallah.progres.data.db.TranscriptSubjectTable
import kotlin.random.Random

data class TranscriptSubjectModel(
  val id: Long,
  val ueId: Long,
  val subjectStringLatin: String,
  val subjectStringArabic: String,
  val coefficient: Double,
  val creditObtained: Double,
  val average: Double?
)

fun TranscriptSubjectsDto.toTable(): TranscriptSubjectTable {
  return TranscriptSubjectTable(
    id = Random.nextLong(),
    subjectStringLatin = mcLibelleFr,
    subjectStringArabic = mcLibelleAr,
    coefficient = coefficient,
    creditObtained = creditObtenu,
    average = moyenneGenerale,
    ueId = bilanUeId
  )
}

fun TranscriptSubjectTable.toModel(): TranscriptSubjectModel {
  return TranscriptSubjectModel(
    id = id,
    ueId = ueId,
    subjectStringLatin = subjectStringLatin,
    subjectStringArabic = subjectStringArabic,
    coefficient = coefficient,
    creditObtained = creditObtained,
    average = average
  )
}
