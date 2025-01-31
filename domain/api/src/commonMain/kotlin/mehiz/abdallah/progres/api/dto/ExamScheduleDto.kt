package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ExamScheduleDto(
  val anonymat: Boolean? = null,
  val dateExamen: String,
  val duree: Long,
  val heureDebut: String,
  val heureFin: String? = null,
  val id: Long? = null,
  val libellePeriode: String,
  val libellePeriodeAr: String,
  val mcLibelleAr: String,
  val mcLibelleFr: String,
  val moyenneSession: Double? = 0.0,
  val noteGereParEnseignant: Boolean? = null,
  val planningCoefficientNoteEliminatoire: Double? = 0.0,
  val typeSession: String,
  val typeSessionAr: String? = null,
)
