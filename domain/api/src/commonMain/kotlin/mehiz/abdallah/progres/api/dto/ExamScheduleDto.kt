package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ExamScheduleDto(
  val anonymat: Boolean? = null,
  val dateExamen: String,
  val duree: Long,
  val heureDebut: String,
  val heureFin: String? = null,
  val id: Long,
  val libellePeriode: String,
  val libellePeriodeAr: String,
  val mcLibelleAr: String,
  val mcLibelleFr: String,
  val moyenneSession: Double,
  val noteGereParEnseignant: Boolean? = null,
  val planningCoefficientNoteEliminatoire: Double,
  val typeSession: String,
  val typeSessionAr: String,
)
