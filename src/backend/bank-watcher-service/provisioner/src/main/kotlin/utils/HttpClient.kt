package utils

import au.com.cding21.security.token.FixedKeyExchangeService
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.util.*

suspend fun HttpClient.authenticatedGetBE(keyService: FixedKeyExchangeService, urlString: String, block: HttpRequestBuilder.() -> Unit): HttpResponse {
    val encryption = keyService.encrypt(System.getenv("BE_API_KEY"))
    val bearerToken = "{\"encrypted\":\"${encryption.first}\",\"timeToken\":\"${encryption.second}\"}"

    return this.get(urlString) {
        headers {
            append(HttpHeaders.Authorization, "Bearer ${Base64.getEncoder().encodeToString(bearerToken.toByteArray())}")
        }
        block()
    }
}