package au.com.cding21.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

class JwtTokenService : TokenService {
    override fun generate(
        config: TokenConfig,
        hash: String,
        vararg claims: TokenClaim,
    ): String {
        var token =
            JWT.create()
                .withAudience(config.audience)
                .withIssuer(config.issuer)
                .withExpiresAt(Date(System.currentTimeMillis() + config.expiresIn))
        claims.forEach {
            token = token.withClaim(it.name, it.value)
        }
        // Sign with token secret and the user's hashed password (salted) as the secret, using this approach ensure
        // upon password change, all existing tokens are invalidated.
        return token.sign(Algorithm.HMAC256(config.secret + hash))
    }
}
