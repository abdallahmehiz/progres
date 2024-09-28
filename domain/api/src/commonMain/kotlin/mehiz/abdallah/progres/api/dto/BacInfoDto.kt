package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class BacInfoDto(
  val anneeBac: String,
  val dateNaissance: String,
  val id: Long,
  val libelleSerieBac: String,
  val matricule: String,
  val moyenneBac: String,
  val nin: String,
  val nomAr: String,
  val nomFr: String,
  val prenomAr: String,
  val prenomFr: String,
  val refCodeSerieBac: String,
  val refCodeWilayaBac: String,
  val uuid: String,
)
