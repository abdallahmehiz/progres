package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class SubjectScheduleDto(
  val ap: String,
  val associationGroupePedagogiqueId: Int,
  val emploiId: Int,
  // val enseignantDisponible: Any,
  val enseignantId: Int?,
  // val enseignantNom: Any,
  // val enseignantPrenom: Any,
  val groupe: String,
  // val groupeDisponible: Any,
  val groupePedagogiqueId: Int,
  val id: Int,
  val jourCode: String?,
  val jourId: Int?,
  val jourLibelleAr: String?,
  val jourLibelleFr: String?,
  val libelleSeance: String,
  // val lieuDisponible: Any,
  val matiere: String,
  val matiereAr: String,
  val nomEnseignantArabe: String?,
  val nomEnseignantLatin: String?,
  val periodeId: Int,
  val plageHoraireHeureDebut: String?,
  val plageHoraireHeureFin: String?,
  val plageHoraireId: Int?,
  val plageHoraireLibelleFr: String?,
  val prenomEnseignantArabe: String?,
  val prenomEnseignantLatin: String?,
  val refLieuDesignation: String?,
  val refLieuId: Int?,
  val refLieuTypeLibelleLongFr: String?,
  val sectionId: Int?,
)
