package mehiz.abdallah.progres.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.kodein.di.DI.Module
import org.kodein.di.bindSingleton

expect val engine: HttpClientEngine

val ApiModule = Module("ApiModule") {
  bindSingleton {
    ProgresApi(
      HttpClient(engine) {
        install(Logging) {
          level = LogLevel.INFO
        }
        install(ContentNegotiation) {
          json(
            Json {
              ignoreUnknownKeys = true
            },
          )
        }
      },
    )
  }
}
