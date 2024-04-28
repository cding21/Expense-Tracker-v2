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
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

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

    routing {
        route(System.getenv("API_VERSION") ?: "/api/v0"){
            // Health check
            get("/health") {
                call.respondText("Hello World!")
            }
            // Authentication
            authRoutes(
                hashingService,
                userService,
                tokenService,
                tokenConfig
            )

            authenticate {
                // Feature Routes
                transactionRoutes(transactionService)

                // Swagger UI
                swaggerUI(path = "openapi")
            }


        }
    }
}
