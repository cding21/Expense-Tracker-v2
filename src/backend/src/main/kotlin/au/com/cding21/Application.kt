package au.com.cding21

import au.com.cding21.plugins.*
import au.com.cding21.routes.configureRouting
import au.com.cding21.security.token.TokenConfig
import au.com.cding21.util.connectToMongoDB
import au.com.cding21.util.connectToPostgres
import io.ktor.server.application.*


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val db = connectToMongoDB()

    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        // Expires in 24 hours
        expiresIn = 1000L * 60L * 60L * 24L,
        secret = System.getenv("JWT_SECRET")
    )

    configureSecurity(tokenConfig, db)
    configureSerialization()
    configureHTTP()
    configureRouting(db, tokenConfig)
}
