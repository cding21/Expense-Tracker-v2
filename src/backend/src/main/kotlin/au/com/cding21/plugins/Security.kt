package au.com.cding21.plugins

import au.com.cding21.security.token.TokenConfig
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity(config: TokenConfig) {
    authentication {
            jwt {
                realm = this@configureSecurity.environment.config.property("jwt.realm").getString()
                verifier(
                    JWT
                        .require(Algorithm.HMAC256(config.secret))
                        .withAudience(config.audience)
                        .withIssuer(config.issuer)
                        .build()
                )
                validate { jwtCredential ->
                    if (jwtCredential.payload.audience.contains(config.audience)) {
                        JWTPrincipal(jwtCredential.payload)
                    } else {
                        null
                    }

                }
            }
        }
}
