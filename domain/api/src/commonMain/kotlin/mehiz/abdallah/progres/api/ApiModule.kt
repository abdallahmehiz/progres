package mehiz.abdallah.progres.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.kodein.di.DI.Module
import org.kodein.di.bindSingleton
import org.kodein.di.bindSingletonOf
import org.kodein.di.instance

expect val engine: HttpClientEngine

val ApiModule = Module("ApiModule") {
  bindSingleton {
    Json {
      ignoreUnknownKeys = true
    }
  }
  bindSingleton {
    HttpClient(engine) {
      install(Logging) {
        level = LogLevel.ALL
        logger = Logger.SIMPLE
      }
      install(ContentNegotiation) {
        json(instance())
      }
    }
  }
  bindSingletonOf(::ProgresApi)
}
