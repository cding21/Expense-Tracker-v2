package auth

import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.configureAuth() {
    install(Authentication) {
        bearer("auth-bearer") {
            realm = "Access to bank-watcher-service"
            authenticate { token ->
                if (token.token != "") {
                    UserIdPrincipal("API")
                } else {
                    null
                }
            }
        }
    }
}