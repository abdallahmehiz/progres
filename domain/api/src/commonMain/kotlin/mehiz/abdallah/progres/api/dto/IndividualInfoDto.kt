package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class IndividualInfoDto(
  val dateNaissance: String,
  val email: String? = null,
  val id: Long,
  val idCarde: String? = null,
  val identifiant: String,
  val lieuNaissance: String? = null,
  val lieuNaissanceArabe: String? = null,
  val nomArabe: String,
  val nomLatin: String,
  val photo: String? = null,
  val prenomArabe: String,
  val prenomLatin: String,
)
