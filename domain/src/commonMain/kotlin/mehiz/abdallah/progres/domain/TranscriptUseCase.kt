package mehiz.abdallah.progres.domain

import mehiz.abdallah.progres.api.ProgresApi
import mehiz.abdallah.progres.data.daos.AcademicDecisionDao
import mehiz.abdallah.progres.data.daos.TranscriptDao
import mehiz.abdallah.progres.data.daos.TranscriptSubjectDao
import mehiz.abdallah.progres.data.daos.TranscriptUEDao
import mehiz.abdallah.progres.data.db.AcademicDecisionTable
import mehiz.abdallah.progres.data.db.TranscriptSubjectTable
import mehiz.abdallah.progres.data.db.TranscriptTable
import mehiz.abdallah.progres.data.db.TranscriptUETable
import mehiz.abdallah.progres.domain.models.AcademicDecisionModel
import mehiz.abdallah.progres.domain.models.TranscriptModel
import mehiz.abdallah.progres.domain.models.toModel
import mehiz.abdallah.progres.domain.models.toTable
import kotlin.uuid.ExperimentalUuidApi

@Suppress("LongParameterList")
class TranscriptUseCase(
  private val api: ProgresApi,
  private val transcriptDao: TranscriptDao,
  private val transcriptUEDao: TranscriptUEDao,
  private val transcriptSubjectDao: TranscriptSubjectDao,
  private val academicDecisionDao: AcademicDecisionDao,
  private val academicPeriodUseCase: AcademicPeriodUseCase,
  private val studentCardUseCase: StudentCardUseCase,
  private val userAuthUseCase: UserAuthUseCase
) {
  @OptIn(ExperimentalUuidApi::class)
  suspend fun getAllTranscripts(refresh: Boolean, propagateRefresh: Boolean): List<TranscriptModel> {
    val academicPeriods = academicPeriodUseCase.getAcademicPeriods(propagateRefresh, true)
    transcriptDao.getAllTranscripts().let { transcripts ->
      if (transcripts.isEmpty() && !refresh) return@let
      return transcripts.map { transcript ->
        val ues = transcriptUEDao.getUEsByTranscriptId(transcript.id)
        if (ues.isEmpty()) return@let
        transcript.toModel(
          period = academicPeriods.first { it.yearPeriodCode == transcript.yearPeriodCode },
          ues.map { ue ->
            val subjects = transcriptSubjectDao.getSubjectsByUEId(ue.id)
            if (subjects.isEmpty()) return@let
            ue.toModel(subjects.map { subject -> subject.toModel() })
          },
        )
      }
    }
    val cards = studentCardUseCase.getAllStudentCards(false) // refreshed on the first line of the function
    val token = userAuthUseCase.getToken()
    val uuid = userAuthUseCase.getUuid()
    val transcripts = mutableListOf<TranscriptTable>()
    val ues = mutableListOf<TranscriptUETable>()
    val subjects = mutableListOf<TranscriptSubjectTable>()
    cards.forEach { card ->
      api.getAcademicTranscripts(uuid, card.id, token).forEach { transcript ->
        academicPeriods.first {
          it.oofId == card.openingTrainingOfferId
        }.let { transcripts.add(transcript.toTable(it.yearPeriodCode)) }
        ues.addAll(transcript.bilanUes.map { it.toTable() })
        transcript.bilanUes.forEach {
          subjects.addAll(it.bilanMcs.map { it.toTable() })
        }
      }
    }
    if (refresh) {
      transcriptDao.deleteAllTranscripts()
      transcriptUEDao.deleteAllUETranscripts()
      transcriptSubjectDao.deleteAllSubjects()
    }
    return transcripts.map { transcript ->
      transcriptDao.insert(transcript)
      transcript.toModel(
        period = academicPeriods.first { it.yearPeriodCode == transcript.yearPeriodCode },
        ues.mapNotNull { ue ->
          transcriptUEDao.insert(ue)
          if (ue.sessionId != transcript.id) {
            null
          } else {
            ue.toModel(
              subjects.mapNotNull {
                transcriptSubjectDao.insert(it)
                if (it.ueId != ue.id) null else it.toModel()
              },
            )
          }
        },
      )
    }
  }

  @OptIn(ExperimentalUuidApi::class)
  suspend fun getAllAcademicDecisions(refresh: Boolean, propagateRefresh: Boolean): List<AcademicDecisionModel> {
    val academicPeriods = academicPeriodUseCase.getAcademicPeriods(propagateRefresh, true)
    academicDecisionDao.getAllAcademicDecisions().let {
      if (it.isNotEmpty() && !refresh) {
        return it.map { decision ->
          decision.toModel(academicPeriods.first { it.yearPeriodCode == decision.yearPeriodCode })
        }
      }
    }
    val cards = studentCardUseCase.getAllStudentCards(false) // refreshed on the first line of this function
    val uuid = userAuthUseCase.getUuid()
    val token = userAuthUseCase.getToken()
    val decisions = mutableListOf<AcademicDecisionTable>()
    cards.forEach { card ->
      api.getAcademicDecision(uuid, card.id, token)?.let {
        academicPeriods.firstOrNull { card.openingTrainingOfferId == it.oofId }?.yearPeriodCode?.let { id ->
          decisions.add(it.toTable(id))
        }
      }
    }
    if (refresh) academicDecisionDao.deleteAllAcademicDecisions()
    decisions.forEach(academicDecisionDao::insert)
    return decisions.map { decision ->
      decision.toModel(
        academicPeriods.first {
          it.yearPeriodCode == decision.yearPeriodCode
        },
      )
    }
  }
}
