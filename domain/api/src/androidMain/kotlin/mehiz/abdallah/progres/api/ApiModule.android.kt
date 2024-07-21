package mehiz.abdallah.progres.api

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.engine.okhttp.OkHttpEngine

actual val engine: HttpClientEngine = OkHttpEngine(OkHttpConfig())
