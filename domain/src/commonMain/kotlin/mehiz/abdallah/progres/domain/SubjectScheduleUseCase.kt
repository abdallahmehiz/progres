package mehiz.abdallah.progres.domain

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.toLocalDateTime
import mehiz.abdallah.progres.api.ProgresApi
import mehiz.abdallah.progres.data.daos.SubjectScheduleDao
import mehiz.abdallah.progres.data.db.SubjectScheduleTable
import mehiz.abdallah.progres.domain.models.SubjectScheduleModel
import mehiz.abdallah.progres.domain.models.toModel
import mehiz.abdallah.progres.domain.models.toTable

class SubjectScheduleUseCase(
  private val api: ProgresApi,
  private val subjectScheduleDao: SubjectScheduleDao,
  private val academicPeriodUseCase: AcademicPeriodUseCase,
  private val studentCardUseCase: StudentCardUseCase,
  private val userAuthUseCase: UserAuthUseCase,
) {

  suspend fun getNextSubjectSchedule(): SubjectScheduleModel? {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val currentDay = now.dayOfWeek
    val currentTime = LocalTime(now.hour, now.minute)

    val upcomingClasses = getCurrentAcademicPeriodSchedules().filter { subject ->
      val subjectDay = subject.day
      subjectDay != null && (
        subjectDay > currentDay ||
          (subjectDay == currentDay && subject.hourlyRangeStart != null && subject.hourlyRangeStart > currentTime)
        )
    }.sortedWith(compareBy({ it.day }, { it.hourlyRangeStart }))

    // Return the first upcoming subject
    return upcomingClasses.firstOrNull()
  }

  suspend fun getCurrentAcademicPeriodSchedules(): List<SubjectScheduleModel> {
    val currentAcademicPeriod = academicPeriodUseCase.getCurrentAcademicPeriod(false, false)
      ?: return emptyList()
    return getAllSubjectsSchedule(false, false).filter { it.period?.id != currentAcademicPeriod.id }
  }

  suspend fun getAllSubjectsSchedule(refresh: Boolean, propagateRefresh: Boolean): List<SubjectScheduleModel> {
    val academicPeriods = academicPeriodUseCase.getAcademicPeriods(refresh, propagateRefresh)
    subjectScheduleDao.getAllSchedules().let {
      if (it.isEmpty() || refresh) return@let
      return it.mapNotNull { subject ->
        academicPeriods.firstOrNull { it.id == subject.periodId }?.let { subject.toModel(it) }
      }
    }
    val studentCards = studentCardUseCase.getAllStudentCards(refresh)
    val token = userAuthUseCase.getToken()
    val subjectsSchedules = mutableListOf<SubjectScheduleTable>()
    studentCards.forEach { card ->
      subjectsSchedules.addAll(
        api.getSubjectsSchedule(card.id, token).map { subject ->
          subject.toTable(
            academicPeriods.first {
              card.openingTrainingOfferId == it.oofId && it.id == subject.periodeId
            }.yearPeriodCode,
          )
        },
      )
    }
    if (refresh) subjectScheduleDao.deleteAllSchedules()
    return subjectsSchedules.map { subject ->
      subjectScheduleDao.insert(subject)
      academicPeriods.firstOrNull { it.id == subject.periodId }.let { subject.toModel(it) }
    }
  }
}

val DayOfWeek.algerianDayNumber: Int
  get() = if (this == DayOfWeek.SUNDAY) 1 else isoDayNumber + 1

val algerianSortedDayOfWeek: ImmutableList<DayOfWeek>
  get() = persistentListOf(
    DayOfWeek.SUNDAY,
    DayOfWeek.MONDAY,
    DayOfWeek.TUESDAY,
    DayOfWeek.WEDNESDAY,
    DayOfWeek.THURSDAY,
    DayOfWeek.FRIDAY,
    DayOfWeek.SATURDAY
  )
