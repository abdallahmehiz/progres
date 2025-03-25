package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class SubjectScheduleDto(
  val ap: String,
  val groupe: String,
  val id: Long,
  val jourId: Long?,
  val jourLibelleAr: String?,
  val jourLibelleFr: String?,
  val matiere: String,
  val matiereAr: String,
  val nomEnseignantArabe: String?,
  val nomEnseignantLatin: String?,
  val periodeId: Long,
  val plageHoraireHeureDebut: String?,
  val plageHoraireHeureFin: String?,
  val plageHoraireLibelleFr: String?,
  val prenomEnseignantArabe: String?,
  val prenomEnseignantLatin: String?,
  val refLieuDesignation: String?,
)
