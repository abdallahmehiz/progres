package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class IndividualInfoDto(
  val dateNaissance: String,
  val id: Long,
  val lieuNaissance: String? = null,
  val lieuNaissanceArabe: String? = null,
  val nomArabe: String,
  val nomLatin: String,
  val prenomArabe: String,
  val prenomLatin: String,
)
