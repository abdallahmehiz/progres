package mehiz.abdallah.progres.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class AcademicPeriodDto(
  val code: String,
  val credit: Long,
  val id: Long,
  val libelleCourtAr: String? = null,
  val libelleCourtLt: String? = null,
  val libelleLongAr: String,
  val libelleLongFrCycle: String,
  val libelleLongFrNiveau: String,
  val libelleLongLt: String,
  val ncPeriodeCode: String? = null,
  val ncPeriodeId: Long? = null,
  val ncPeriodeLibelle: String? = null,
  val rang: Long,
  val rangNiveau: Long,
)
