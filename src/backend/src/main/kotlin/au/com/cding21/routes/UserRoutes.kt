package au.com.cding21.routes

import au.com.cding21.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.configureInternalUserRoutes(userService: UserService) {
    /**
     * Internal use only. Get all available users
     */
    authenticate("auth-bearer") {
        get("/users/all") {
            val result = userService.getAllUsers()
            if (result.isEmpty()) {
                call.respond(HttpStatusCode.OK, "[]")
                return@get
            }
            call.respond(HttpStatusCode.OK, userService.getAllUsers())
        }
    }
}