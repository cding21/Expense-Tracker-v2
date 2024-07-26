import au.com.cding21.security.encryption.AESEncryptionServiceImpl
import au.com.cding21.security.encryption.RSAServiceImpl
import au.com.cding21.security.token.UnixTimeBasedSymmetricKeyService
import auth.configureAuth
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import java.util.*

fun main() {
    embeddedServer(Netty, port = 3000, host = "0.0.0.0") {
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
            })
        }
        configureAuth()

        val client = HttpClient(CIO)
        val keyService = UnixTimeBasedSymmetricKeyService(AESEncryptionServiceImpl())

        routing {
            get("/") {
                val encryption = keyService.encrypt(System.getenv("API_KEY"))
                val bearerToken = "{\"encrypted\":\"${encryption.first}\",\"timeToken\":\"${encryption.second}\"}"
                println(bearerToken)
                val response = client.get("http://localhost:8080/api/v0/users/all") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer ${Base64.getEncoder().encodeToString(bearerToken.toByteArray())}")
                    }
                }
                println(response.status)

                call.respond(response.body<String>())
            }
        }
    }.start(true)
}