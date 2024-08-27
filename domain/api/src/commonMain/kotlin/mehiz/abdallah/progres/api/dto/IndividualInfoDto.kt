package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class IndividualInfoDto(
  val dateNaissance: String,
  val email: String? = null,
  val id: Long,
  val idCarde: String,
  val identifiant: String,
  val lieuNaissance: String,
  val lieuNaissanceArabe: String,
  val nomArabe: String,
  val nomLatin: String,
  val photo: String,
  val prenomArabe: String,
  val prenomLatin: String,
)
