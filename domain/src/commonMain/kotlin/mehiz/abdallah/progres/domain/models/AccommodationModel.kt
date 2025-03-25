package mehiz.abdallah.progres.domain.models

import mehiz.abdallah.progres.api.dto.AccommodationDto
import mehiz.abdallah.progres.data.db.AccommodationTable
import kotlin.random.Random

data class AccommodationModel(
  val id: Long,
  val academicYearId: Long,
  val assignedPavillion: String?,
  val providerId: Long,
  val providerStringLatin: String,
  val providerStringArabic: String,
  val residenceStringLatin: String,
  val residenceStringArabic: String,
)

fun AccommodationTable.toModel(): AccommodationModel {
  return AccommodationModel(
    id = id,
    academicYearId = academicYearId,
    providerId = providerId,
    providerStringLatin = providerStringLatin,
    providerStringArabic = providerStringArabic,
    residenceStringLatin = residenceStringLatin,
    residenceStringArabic = residenceStringArabic,
    assignedPavillion = assignedPavillion
  )
}

fun AccommodationDto.toTable(): AccommodationTable {
  return AccommodationTable(
    id = Random.nextLong(),
    academicYearId = idAnneeAcademique,
    providerId = idDou,
    providerStringLatin = llDouLatin,
    providerStringArabic = llDouArabe,
    residenceStringLatin = llResidanceLatin,
    residenceStringArabic = llResidanceArabe,
    assignedPavillion = llAffectation,
  )
}
