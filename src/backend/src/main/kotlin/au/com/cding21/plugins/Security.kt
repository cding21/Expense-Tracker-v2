package au.com.cding21.plugins

import au.com.cding21.security.token.FixedKeyExchangeService
import au.com.cding21.security.token.TokenConfig
import au.com.cding21.services.impl.MongoUserServiceImpl
import au.com.cding21.third_party.banks.util.throwIfNullKey
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.mongodb.client.MongoDatabase
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.litote.kmongo.bson
import org.litote.kmongo.json
import java.security.Security
import java.util.*

fun Application.configureSecurity(
    config: TokenConfig,
    db: MongoDatabase,
    keyService: FixedKeyExchangeService
) {
    val KEY_EXPIRATION = 10000 // 10 seconds
    val userService = MongoUserServiceImpl(db)
    Security.addProvider(BouncyCastleProvider())
    Security.setProperty("crypto.policy", "unlimited");

    authentication {
        jwt("auth-jwt") {
            realm = this@configureSecurity.environment.config.property("jwt.realm").getString()
            verifier{ jwt ->
                // Get user's hashed pw for dynamic hashing signature
                val blob = jwt.json.bson.getString("blob")
                val token = JWT.decode(blob.value.toString())

                val userId = token.claims["userId"].toString().replace("\"", "")
                var userHashedPassword: String?
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
                }
                else {
                     null
                }
            }
        }

        bearer("auth-bearer") {
            realm = "Access to internal APIs"
            authenticate { token ->
                val API_KEY = System.getenv("API_KEY")
                val bearerRequest = Json.decodeFromString<JsonObject>(Base64.getDecoder().decode(token.token).decodeToString())
                val encryptedApiKey = bearerRequest.throwIfNullKey("encrypted").jsonPrimitive.content
                val timeToken = bearerRequest.throwIfNullKey("timeToken").jsonPrimitive.content

                if (System.currentTimeMillis() - timeToken.toLong() > KEY_EXPIRATION) {
                    return@authenticate null
                }
                val decrypted = keyService.decrypt(encryptedApiKey, timeToken)
                if (decrypted.compareTo(API_KEY) == 0) {
                    return@authenticate UserIdPrincipal(decrypted)
                }
                return@authenticate null
            }
        }
    }
}
