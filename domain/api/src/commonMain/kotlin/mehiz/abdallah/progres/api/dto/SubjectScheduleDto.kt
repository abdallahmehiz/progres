package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class SubjectScheduleDto(
  val ap: String,
  val associationGroupePedagogiqueId: Long,
  val emploiId: Long,
  // val enseignantDisponible: Any,
  val enseignantId: Int?,
  // val enseignantNom: Any,
  // val enseignantPrenom: Any,
  val groupe: String,
  // val groupeDisponible: Any,
  val groupePedagogiqueId: Long,
  val id: Long,
  val jourCode: String?,
  val jourId: Long?,
  val jourLibelleAr: String?,
  val jourLibelleFr: String?,
  val libelleSeance: String,
  // val lieuDisponible: Any,
  val matiere: String,
  val matiereAr: String,
  val nomEnseignantArabe: String?,
  val nomEnseignantLatin: String?,
  val periodeId: Long,
  val plageHoraireHeureDebut: String?,
  val plageHoraireHeureFin: String?,
  val plageHoraireId: Long?,
  val plageHoraireLibelleFr: String?,
  val prenomEnseignantArabe: String?,
  val prenomEnseignantLatin: String?,
  val refLieuDesignation: String?,
  val refLieuId: Long?,
  val refLieuTypeLibelleLongFr: String?,
  val sectionId: Long?,
)
