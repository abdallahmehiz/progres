package mehiz.abdallah.progres.api

internal const val BASE_URL = "https://progres.mesrs.dz"

enum class Endpoints(private val endpoint: String) {
  Login("/api/authentication/v1/"),
  GetStudentPhoto("/api/infos/image/{uuid}")
  ;

  // Wanted to use String.format() but that is only for jvm :pain:
  fun buildUrl(vararg params: String): String {
    var url = "$BASE_URL$endpoint"
    val regex = "\\{[^}]+\\}".toRegex()
    params.forEach { url = url.replaceFirst(regex, it) }
    return url
  }
}
