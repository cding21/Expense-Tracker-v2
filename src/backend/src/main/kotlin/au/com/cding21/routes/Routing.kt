package au.com.cding21.routes

import au.com.cding21.security.hashing.SHA256HashingService
import au.com.cding21.security.token.JwtTokenService
import au.com.cding21.security.token.TokenConfig
import au.com.cding21.services.impl.MongoTransactionServiceImpl
import au.com.cding21.services.impl.MongoUserServiceImpl
import com.mongodb.client.MongoDatabase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

fun Application.configureRouting(
    db: MongoDatabase,
    tokenConfig: TokenConfig
) {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
        }
    }

    // Services
    val transactionService = MongoTransactionServiceImpl(db)
    val hashingService = SHA256HashingService()
    val userService = MongoUserServiceImpl(db)
    val tokenService = JwtTokenService()

    val logger = LoggerFactory.getLogger("Routing")

    routing {
        route(System.getenv("API_VERSION") ?: "/api/v0"){
            // Health check
            get("") {
                call.respondText("Hello World!")
            }
            // Authentication
            authRoutes(
                hashingService,
                userService,
                tokenService,
                tokenConfig
            )

            authenticate("auth-jwt") {
                // Route to check if the token is being used from the same IP it was issued for
                intercept(ApplicationCallPipeline.Call) {
                    val principal = call.principal<JWTPrincipal>()

                    val userId = principal?.getClaim("userId", String::class)
                    val tokenIp = principal?.getClaim("ip", String::class)
                    val requestIp = call.request.origin.remoteHost
                    if (!tokenIp.equals(requestIp)) {
                        // Log this event and do nothing
                        logger.atWarn().log("User $userId is trying to access from a different IP. "
                        + "{Request IP: $requestIp, Token IP: $tokenIp}")
                    }
                }

                // Feature Routes
                transactionRoutes(transactionService)

                // Swagger UI
                swaggerUI(path = "openapi")
            }
        }
    }
}
