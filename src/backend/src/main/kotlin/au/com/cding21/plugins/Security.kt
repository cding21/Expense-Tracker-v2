package au.com.cding21.plugins

import au.com.cding21.security.token.TokenConfig
import au.com.cding21.services.impl.MongoUserServiceImpl
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.mongodb.client.MongoDatabase
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.bson
import org.litote.kmongo.json

fun Application.configureSecurity(
    config: TokenConfig,
    db: MongoDatabase
) {
    val userService = MongoUserServiceImpl(db)
    authentication {
            jwt("auth-jwt") {
                realm = this@configureSecurity.environment.config.property("jwt.realm").getString()
                verifier{ jwt ->
                    // Get user's hashed pw for dynamic hashing signature
                    val blob = jwt.json.bson.getString("blob")
                    val token = JWT.decode(blob.value.toString())

                    val userId = token.claims["userId"].toString().replace("\"", "")
                    var userHashedPassword: String? = null
                    runBlocking {
                        val user = userService.getUserById(userId)
                        userHashedPassword = user?.password
                    }
                    JWT
                        .require(Algorithm.HMAC256(config.secret + userHashedPassword))
                        .withAudience(config.audience)
                        .withIssuer(config.issuer)
                        .build()
                }

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
