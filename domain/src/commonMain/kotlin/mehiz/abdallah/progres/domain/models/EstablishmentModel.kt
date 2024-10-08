package mehiz.abdallah.progres.domain.models

import mehiz.abdallah.progres.data.db.EstablishmentTable

data class EstablishmentModel(
  val id: Long,
  val nameLatin: String,
  val nameArabic: String,
  val code: String,
  val photo: ByteArray?,
)

fun EstablishmentTable.toModel(): EstablishmentModel {
  return EstablishmentModel(
    id = id,
    nameLatin = nameLatin,
    nameArabic = nameArabic,
    code = code,
    photo = photo
  )
}
