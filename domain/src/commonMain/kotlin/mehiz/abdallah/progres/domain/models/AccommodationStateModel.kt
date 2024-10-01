package mehiz.abdallah.progres.domain.models

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.DateTimeComponents
import mehiz.abdallah.progres.api.dto.AccommodationStateDto
import mehiz.abdallah.progres.data.db.AccommodationStateTable

data class AccommodationStateModel(
  val id: Long,
  val cardId: Long,
  val studentFolderId: Long,
  val academicYearId: Long,
  val academicYearString: String,
  val providerId: Long,
  val providerStringLatin: String,
  val providerStringArabic: String,
  val residenceId: Long?,
  val residenceStringLatin: String,
  val residenceStringArabic: String,
  val accommodationRequestDate: LocalDateTime?,
  val renewalRequestDate: LocalDateTime?,
  val isARenewalRequest: Boolean,
  val isARenewal: Boolean,
  val isApproved: Boolean,
  val approvalDate: LocalDateTime?,
  val assignedPavillion: String,
  val isAccommodationPaid: Boolean
)

fun AccommodationStateTable.toModel(): AccommodationStateModel {
  return AccommodationStateModel(
    id = id,
    cardId = cardId,
    studentFolderId = studentFolderId,
    academicYearId = academicYearId,
    academicYearString = academicYearString,
    providerId = providerId,
    providerStringLatin = providerStringLatin,
    providerStringArabic = providerStringArabic,
    residenceId = residenceId,
    residenceStringLatin = residenceStringLatin,
    residenceStringArabic = residenceStringArabic,
    accommodationRequestDate = DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse(
      accommodationRequestDate
    ).toLocalDateTime(),
    renewalRequestDate = renewalRequestDate?.let {
      DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse(it).toLocalDateTime()
    },
    isARenewalRequest = isARenewalRequest == 1L,
    isARenewal = isARenewal == 1L,
    isApproved = isApproved == 1L,
    approvalDate = approvalDate?.let { DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse(it).toLocalDateTime() },
    assignedPavillion = assignedPavillion,
    isAccommodationPaid = isAccommodationPaid == 1L
  )
}

fun AccommodationStateDto.toTable(): AccommodationStateTable {
  return AccommodationStateTable(
    id = id,
    cardId = idDia,
    studentFolderId = idDossierEtudiant,
    academicYearId = idAnneeAcademique,
    academicYearString = codeAnneeAcademique,
    providerId = idDou,
    providerStringLatin = llDou,
    providerStringArabic = llDouArabe,
    residenceId = idResidence,
    residenceStringLatin = llResidance,
    residenceStringArabic = llResidanceArabe,
    accommodationRequestDate = dateDemandeHeb,
    renewalRequestDate = dateDemandeRenouvellement,
    isARenewalRequest = if (demandeRenouvellement == true) 1 else 0,
    isARenewal = if (renouvellement == true) 1 else 0,
    isApproved = if (approuveeHebDou || approuveeHebDou1) 1 else 0,
    approvalDate = dateApprouveHebDou,
    assignedPavillion = llAffectation,
    isAccommodationPaid = if (hebergementPaye) 1 else 0
  )
}
