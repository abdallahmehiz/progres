package mehiz.abdallah.progres.domain.models

import kotlinx.datetime.LocalDate
import mehiz.abdallah.progres.api.dto.StudentCardDto
import mehiz.abdallah.progres.data.db.StudentCardTable

data class StudentCardModel(
  val academicYearString: String,
  val academicYearId: Long,
  val cycleStringLatin: String,
  val cycleStringArabic: String,
  val id: Long,
  val individualFirstNameArabic: String,
  val individualFirstNameLatin: String,
  val individualLastNameArabic: String,
  val individualLastNameLatin: String,
  val individualDateOfBirth: LocalDate,
  val individualPlaceOfBirthArabic: String?,
  val individualPlaceOfBirthLatin: String?,
  val establishmentStringArabic: String,
  val establishmentStringLatin: String,
  val establishmentLogo: ByteArray?,
  val levelId: Long,
  val levelStringLongArabic: String,
  val levelStringLongLatin: String,
  val registrationNumber: String,
  val ofDomainStringArabic: String?,
  val ofDomainStringLatin: String?,
  val ofFieldStringArabic: String?,
  val ofFieldStringLatin: String?,
  val ofSpecialtyStringLatin: String?,
  val ofSpecialtyStringArabic: String?,
  val openingTrainingOfferId: Long,
  val photo: ByteArray?,
  val isTransportPaid: Boolean,
  val situationId: String,
)

fun StudentCardDto.toTable(
  isTransportPaid: Boolean,
  establishmentLogoPath: String?,
): StudentCardTable {
  return StudentCardTable(
    academicYearString = anneeAcademiqueCode,
    academicYearId = anneeAcademiqueId,
    id = id,
    individualFirstNameArabic = individuPrenomArabe,
    individualFirstNameLatin = individuPrenomLatin,
    individualLastNameArabic = individuNomArabe,
    individualLastNameLatin = individuNomLatin,
    individualDateOfBirth = individuDateNaissance,
    individualPlaceOfBirthArabic = individuLieuNaissanceArabe,
    individualPlaceOfBirthLatin = individuLieuNaissance,
    levelStringLongArabic = niveauLibelleLongAr,
    levelStringLongLatin = niveauLibelleLongLt,
    registrationNumber = numeroInscription,
    ofDomainStringArabic = ofLlDomaineArabe,
    ofDomainStringLatin = ofLlDomaine,
    ofFieldStringArabic = ofLlFiliereArabe,
    ofFieldStringLatin = ofLlFiliere,
    ofSpecialtyStringLatin = ofLlSpecialite,
    ofSpecialtyStringArabic = ofLlSpecialiteArabe,
    openingTrainingOfferId = ouvertureOffreFormationId,
    isTransportPaid = if (isTransportPaid) 1L else 0L,
    situationId = situationId.toString(),
    levelId = niveauId,
    establishmentStringLatin = llEtablissementLatin,
    establishmentStringArabic = llEtablissementArabe,
    establishmentLogoPath = establishmentLogoPath,
    cycleStringLatin = refLibelleCycle,
    cycleStringArabic = refLibelleCycleAr,
  )
}

fun StudentCardTable.toModel(
  photo: ByteArray?,
): StudentCardModel {
  return StudentCardModel(
    academicYearString = academicYearString,
    academicYearId = academicYearId,
    id = id,
    individualFirstNameArabic = individualFirstNameArabic,
    individualFirstNameLatin = individualFirstNameLatin,
    individualLastNameArabic = individualLastNameArabic,
    individualLastNameLatin = individualLastNameLatin,
    individualDateOfBirth = LocalDate.parse(individualDateOfBirth),
    individualPlaceOfBirthArabic = individualPlaceOfBirthArabic,
    individualPlaceOfBirthLatin = individualPlaceOfBirthLatin,
    levelId = levelId,
    levelStringLongArabic = levelStringLongArabic,
    levelStringLongLatin = levelStringLongLatin,
    registrationNumber = registrationNumber,
    ofDomainStringArabic = ofDomainStringArabic,
    ofDomainStringLatin = ofDomainStringLatin,
    ofFieldStringArabic = ofFieldStringArabic,
    ofFieldStringLatin = ofFieldStringLatin,
    ofSpecialtyStringArabic = ofSpecialtyStringArabic,
    ofSpecialtyStringLatin = ofSpecialtyStringLatin,
    openingTrainingOfferId = openingTrainingOfferId,
    photo = photo,
    isTransportPaid = isTransportPaid == 1L,
    situationId = situationId,
    establishmentStringLatin = establishmentStringLatin,
    establishmentStringArabic = establishmentStringLatin,
    establishmentLogo = null, // Will be loaded separately by use case
    cycleStringLatin = cycleStringLatin,
    cycleStringArabic = cycleStringArabic
  )
}
