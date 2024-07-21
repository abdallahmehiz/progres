package mehiz.abdallah.progres.domain

import mehiz.abdallah.progres.api.ProgresApi

class AccountUseCase(
  private val api: ProgresApi
) {
  suspend fun login(id: String, password: String) {
    api.login(id, password)
  }
}
