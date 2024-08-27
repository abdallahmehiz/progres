package mehiz.abdallah.progres.domain.models

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.DateTimeComponents
import mehiz.abdallah.progres.api.dto.IndividualInfoDto
import mehiz.abdallah.progres.data.db.IndividualInfoTable

data class IndividualInfoModel(
  val id: Long,
  val identifier: String,
  val dateOfBirth: LocalDateTime,
  val firstNameArabic: String,
  val firstNameLatin: String,
  val lastNameArabic: String,
  val lastNameLatin: String,
  val placeOfBirthArabic: String,
  val placeOfBirthLatin: String,
  val cardId: String, // idk what this is for,
  val photo: ByteArray
)

fun IndividualInfoDto.toTable(
  photo: ByteArray,
): IndividualInfoTable {
  return IndividualInfoTable(
    id = id,
    identifier = identifiant,
    dateOfBirth = dateNaissance,
    placeOfBirthArabic = lieuNaissanceArabe,
    placeOfBirthLatin = lieuNaissance,
    firstNameArabic = prenomArabe,
    firstNameLatin = prenomLatin,
    lastNameLatin = nomLatin,
    lastNameArabic = nomArabe,
    photo = photo,
    cardId = idCarde
  )
}

fun IndividualInfoTable.toModel(): IndividualInfoModel {
  return IndividualInfoModel(
    id = id,
    identifier = identifier,
    dateOfBirth = DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse(dateOfBirth).toLocalDateTime(),
    placeOfBirthArabic = placeOfBirthArabic,
    placeOfBirthLatin = placeOfBirthLatin,
    firstNameArabic = firstNameArabic,
    firstNameLatin = firstNameLatin,
    lastNameArabic = lastNameArabic,
    lastNameLatin = lastNameLatin,
    cardId = cardId,
    photo = photo
  )
}
