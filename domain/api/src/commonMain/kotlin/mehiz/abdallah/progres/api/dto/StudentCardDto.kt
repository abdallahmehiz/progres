package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class StudentCardDto(
  val anneeAcademiqueCode: String,
  val anneeAcademiqueId: Long,
  val id: Long,
  val individuDateNaissance: String,
  val individuLieuNaissance: String,
  val individuLieuNaissanceArabe: String,
  val individuNomArabe: String,
  val individuNomLatin: String,
  val individuPrenomArabe: String,
  val individuPrenomLatin: String,
  val llEtablissementArabe: String,
  val llEtablissementLatin: String,
  val niveauId: Long,
  val niveauLibelleLongAr: String,
  val niveauLibelleLongLt: String,
  val numeroInscription: String,
  val ofLlDomaine: String? = null,
  val ofLlDomaineArabe: String? = null,
  val ofLlFiliere: String? = null,
  val ofLlFiliereArabe: String? = null,
  val ofLlSpecialite: String? = null,
  val ofLlSpecialiteArabe: String? = null,
  val ouvertureOffreFormationId: Long,
  val refLibelleCycle: String,
  val refLibelleCycleAr: String,
  val situationId: Long,
  val uuid: String,
)
