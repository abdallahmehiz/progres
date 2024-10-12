package utils

import io.ktor.client.network.sockets.SocketTimeoutException
import java.net.SocketException
import java.net.UnknownHostException

// timeouts, can't reach and connection resets
actual val Throwable.isNetworkError: Boolean
  get() = this is UnknownHostException || this is SocketTimeoutException || this is SocketException
