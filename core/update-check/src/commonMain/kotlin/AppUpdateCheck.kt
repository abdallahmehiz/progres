import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubRelease(
  @SerialName("tag_name")
  val tagName: String,
)

@Suppress("OPT_IN_USAGE")
class AppUpdateCheck(
  private val client: HttpClient,
  private val currentTag: String,
  private val owner: String,
  private val repo: String,
) {
  private val _isThereAnUpdate = MutableStateFlow(false)
  val isThereAnUpdate = _isThereAnUpdate.asStateFlow()

  init {
    // Tbh, i am not interested in a more sophisticated approach
    GlobalScope.launch(Dispatchers.IO) {
      val url = "https://api.github.com/repos/$owner/$repo/releases/latest"
      _isThereAnUpdate.update {
        try {
          val response = client.get(url)
          val latestRelease = response.body<GitHubRelease>()
          println("Latest release: ${latestRelease.tagName}")
          latestRelease.tagName != currentTag
        } catch (e: Exception) {
          e.printStackTrace()
          false
        }
      }
    }
  }
}
