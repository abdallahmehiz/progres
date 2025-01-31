package mehiz.abdallah.progres.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

expect val engine: HttpClientEngine

val ApiModule = module {
  single {
    Json {
      ignoreUnknownKeys = true
      isLenient = true
    }
  }
  single {
    HttpClient(engine) {
      followRedirects = true
      install(Logging) {
        level = LogLevel.INFO
        logger = Logger.DEFAULT
      }
      install(ContentNegotiation) {
        json(get())
      }
    }
  }
  singleOf(::ProgresApi)
}
