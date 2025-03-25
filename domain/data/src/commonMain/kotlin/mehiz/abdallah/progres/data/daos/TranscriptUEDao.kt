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
        ueStringLatin = ueStringLatin,
        ueStringArabic = ueStringArabic,
        average = average,
        credit = credit,
        creditAcquired = creditAcquired,
        ueNatureLatin = ueNatureLatin,
        ueNatureArabic = ueNatureArabic,
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
