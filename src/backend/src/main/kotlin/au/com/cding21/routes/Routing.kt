package au.com.cding21.routes

import au.com.cding21.services.TransactionService
import com.mongodb.client.MongoDatabase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(db: MongoDatabase) {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
        }
    }

    // Services
    val transactionService = TransactionService(db)

    routing {
        route(System.getenv("API_VERSION") ?: "/api/v0"){
            // Health check
            get("/health") {
                call.respondText("Hello World!")
            }
            
            // Feature Routes
            transactionRoutes(transactionService)

            // Swagger UI
            swaggerUI(path = "openapi")
        }
    }
}
