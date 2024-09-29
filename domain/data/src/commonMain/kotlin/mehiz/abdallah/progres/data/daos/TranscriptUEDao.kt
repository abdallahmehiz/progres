package mehiz.abdallah.progres.data.daos

import mehiz.abdallah.progres.data.db.ProgresDB
import mehiz.abdallah.progres.data.db.TranscriptUETable

class TranscriptUEDao(
  db: ProgresDB
) {

  private val queries = db.transcriptUETableQueries

  fun insert(table: TranscriptUETable) {
    with(table) {
      queries.insert(
        id = id,
        sessionId = sessionId,
        rapartitionUEId = rapartitionUEId,
        ueStringLatin = ueStringLatin,
        ueCode = ueCode,
        ueType = ueType,
        average = average,
        coefficient = coefficient,
        credit = credit,
        creditObtained = creditObtained,
        creditAcquired = creditAcquired,
        ueNatureLatin = ueNatureLatin,
        ueNatureArabic = ueNatureArabic,
        ueNatureCode = ueNatureCode,
        uePassingGrade = uePassingGrade,
        ueAcquired = ueAcquired,
      )
    }
  }

  fun getAllUETranscripts(): List<TranscriptUETable> {
    return queries.getAllTransriptUEs().executeAsList()
  }

  fun getUEsByTranscriptId(transcriptId: Long): List<TranscriptUETable> {
    return queries.getTranscriptsUEsByTranscriptId(transcriptId).executeAsList()
  }

  fun deleteAllUETranscripts() {
    queries.deleteAllTranscriptUEs()
  }

  fun getUEById(id: Long): TranscriptUETable? {
    return queries.getTranscriptUEById(id).executeAsOneOrNull()
  }
}
