package mehiz.abdallah.progres.data.daos

import mehiz.abdallah.progres.data.db.ProgresDB
import mehiz.abdallah.progres.data.db.TranscriptTable

class TranscriptDao(
  db: ProgresDB
) {

  private val queries = db.transcriptTableQueries

  fun insert(table: TranscriptTable) {
    with(table) {
      queries.insert(
        id = id,
        type = type,
        periodId = periodId,
        periodStringLatin = periodStringLatin,
        periodStringArabic = periodStringArabic,
        average = average,
        averageSn = averageSn,
        credit = credit,
        creditObtained = creditObtained,
        creditAcquired = creditAcquired,
        isAnnual = isAnnual,
        cycleStringLatin = cycleStringLatin,
        levelCode = levelCode,
        levelRank = levelRank,
        levelStringLatin = levelStringLatin,
        levelStringArabic = levelStringArabic,
        coefficient = coefficient
      )
    }
  }

  fun getAllTranscripts(): List<TranscriptTable> {
    return queries.getAllTranscripts().executeAsList()
  }

  fun deleteAllTranscripts() {
    queries.deleteTranscripts()
  }

  fun getTranscriptById(id: Long): TranscriptTable? {
    return queries.getTranscriptById(id).executeAsOneOrNull()
  }
}
