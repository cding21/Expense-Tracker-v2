package au.com.cding21.security

import au.com.cding21.security.hashing.SHA256HashingService
import au.com.cding21.security.token.JwtTokenService
import au.com.cding21.security.token.TokenConfig
import au.com.cding21.services.impl.MongoUserServiceImpl
import com.mongodb.client.MongoDatabase
import io.ktor.server.application.*

fun Application.configureAuthentication(
    db: MongoDatabase
) {
    val userDataSource = MongoUserServiceImpl(db)
    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 1000L * 60L * 60L * 24L,
        secret = System.getenv("JWT_SECRET")
    )

    val hashingService = SHA256HashingService()

}