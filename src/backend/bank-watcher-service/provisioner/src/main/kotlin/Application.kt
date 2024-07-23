import auth.configureAuth
import org.slf4j.LoggerFactory
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.callloging.*
import kotlinx.serialization.json.Json
import org.slf4j.event.Level

fun main() {
    embeddedServer(Netty, port = 3000, host = "0.0.0.0") {
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
            })
        }
        configureAuth()

        routing {
            authenticate("auth-bearer") {
                get("/") {
                    LoggerFactory.getLogger("abc").info("call.request.origin.remoteHost")
                    call.respond("Hello World!")
                }
            }
        }
    }.start(true)
}