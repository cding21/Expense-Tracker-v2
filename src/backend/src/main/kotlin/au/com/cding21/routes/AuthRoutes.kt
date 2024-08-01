package au.com.cding21.routes

import au.com.cding21.data.User
import au.com.cding21.data.requests.AuthRequest
import au.com.cding21.data.responses.AuthResponse
import au.com.cding21.security.hashing.HashingService
import au.com.cding21.security.hashing.SaltedHash
import au.com.cding21.security.token.TokenClaim
import au.com.cding21.security.token.TokenConfig
import au.com.cding21.security.token.TokenService
import au.com.cding21.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes(
    hashingService: HashingService,
    userService: UserService,
    tokenService: TokenService,
    tokenConfig: TokenConfig,
) {
    authenticate("auth-jwt") {
        get("/authenticate") {
            call.respond(HttpStatusCode.OK)
        }
        get("/secret") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            call.respond(HttpStatusCode.OK, "$userId")
        }
    }

    post("/signup") {
        val req = call.receive<AuthRequest>()
        if (!validateAuthRequest(req)) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val saltedHash = hashingService.generateSaltedHash(req.password)
        val newUser =
            User(
                username = req.username,
                password = saltedHash.hash,
                salt = saltedHash.salt,
            )
        val wasAcknowledged = userService.createUser(newUser)
        if (!wasAcknowledged) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }
        call.respond(HttpStatusCode.OK)
    }

    post("/login") {
        val req = call.receive<AuthRequest>()

        val user = userService.getUserByUsername(req.username)
        if (user == null) {
            call.respond(HttpStatusCode.Conflict, "Incorrect username or password")
            return@post
        }
        val isValid = hashingService.verify(req.password, SaltedHash(user.password, user.salt))
        if (!isValid) {
            call.respond(HttpStatusCode.Conflict, "Incorrect username or password")
            return@post
        }
        val token =
            tokenService.generate(
                tokenConfig,
                user.password,
                TokenClaim(
                    name = "userId",
                    value = user.id.toString(),
                ),
                TokenClaim(
                    name = "ip",
                    value = call.request.origin.remoteHost,
                ),
            )
        call.respond(HttpStatusCode.OK, AuthResponse(token))
    }
}

fun validateAuthRequest(user: AuthRequest): Boolean {
    return user.username.isNotEmpty() &&
        user.password.isNotEmpty() &&
        user.password.length >= 8 &&
        user.password.matches(Regex(".*[a-z].*")) &&
        user.password.matches(Regex(".*[0-9].*")) &&
        user.password.matches(Regex(".*[!@#\$%^&*()].*"))
}
