import au.com.cding21.security.encryption.AESEncryptionServiceImpl
import au.com.cding21.security.token.UnixTimeBasedSymmetricKeyService
import auth.configureAuth
import redis.RedisAsyncClient
import db.configureRedis
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import utils.authenticatedGetBE

fun main() {
    embeddedServer(Netty, port = 3000, host = "0.0.0.0") {
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
            })
        }
        configureAuth()
        val redisCommand = configureRedis()

        val client = HttpClient(CIO)
        val keyService = UnixTimeBasedSymmetricKeyService(AESEncryptionServiceImpl())
        val redisClient = RedisAsyncClient(redisCommand)

        routing {
            get("/") {
                val response = client.authenticatedGetBE(keyService, "http://localhost:8080/api/v0/users/all") {}
                println(response.status)
                call.respond(response.body<String>())
            }
        }
    }.start(true)
}