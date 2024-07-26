package auth

import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.configureAuth() {
    install(Authentication) {
        bearer("auth-bearer") {
            realm = "Access to bank-watcher-service"
            authenticate { token ->
                if (token.token != System.getenv("API_KEY")) {
                    UserIdPrincipal(System.getenv("API_KEY"))
                }
                null
            }
        }
    }
}