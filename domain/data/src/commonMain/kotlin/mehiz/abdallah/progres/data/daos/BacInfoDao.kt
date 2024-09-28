package mehiz.abdallah.progres.data.daos

import mehiz.abdallah.progres.data.db.BacInfoTable
import mehiz.abdallah.progres.data.db.ProgresDB

class BacInfoDao(
  db: ProgresDB
) {

  val queries = db.bacInfoTableQueries

  fun insert(table: BacInfoTable) {
    with(table) {
      queries.insert(
        id = id,
        uuid = uuid,
        nationalID = nationalID,
        registrationNumber = registrationNumber,
        firstNameLatin = firstNameLatin,
        firstNameArabic = firstNameArabic,
        lastNameLatin = lastNameLatin,
        lastNameArabic = lastNameArabic,
        birthDate = birthDate,
        stateCode = stateCode,
        seriesCode = seriesCode,
        seriesStringLatin = seriesStringLatin,
        bacYear = bacYear,
        grade = grade
      )
    }
  }

  fun get(): BacInfoTable? {
    return queries.get().executeAsOneOrNull()
  }

  fun delete() {
    queries.delete()
  }
}
