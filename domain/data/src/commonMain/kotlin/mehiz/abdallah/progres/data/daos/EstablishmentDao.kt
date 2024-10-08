package mehiz.abdallah.progres.data.daos

import mehiz.abdallah.progres.data.db.EstablishmentTable
import mehiz.abdallah.progres.data.db.ProgresDB

class EstablishmentDao(
  db: ProgresDB
) {
  val queries = db.establishmentTableQueries

  fun insert(table: EstablishmentTable) {
    with(table) {
      queries.insert(
        id = id,
        nameLatin = nameLatin,
        nameArabic = nameArabic,
        code = code,
        photo = photo
      )
    }
  }

  fun getAllEstablishments(): List<EstablishmentTable> {
    return queries.getAllEstablishments().executeAsList()
  }

  fun getEstablishmentById(id: Long): EstablishmentTable? {
    return queries.getEstablishmentById(id).executeAsOneOrNull()
  }

  fun deleteAllEstablishments() {
    queries.delete()
  }

  fun getEstablishmentPhotoById(id: Long): ByteArray? {
    return queries.getEstablishmentPhotoById(id).executeAsOneOrNull()?.photo
  }
}
