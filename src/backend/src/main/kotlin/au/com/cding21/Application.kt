package au.com.cding21

import au.com.cding21.plugins.*
import au.com.cding21.routes.configureRouting
import au.com.cding21.security.token.TokenConfig
import au.com.cding21.util.connectToMongoDB
import io.ktor.server.application.*
import io.ktor.server.websocket.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(WebSockets)
    val db = connectToMongoDB()

    val tokenConfig =
        TokenConfig(
            issuer = environment.config.property("jwt.issuer").getString(),
            audience = environment.config.property("jwt.audience").getString(),
            expiresIn = environment.config.property("jwt.expiresIn").getString().toLong(),
            secret = environment.config.property("jwt.secret").getString(),
        )

    configureSecurity(tokenConfig, db)
    configureSerialization()
    configureHTTP()
    configureRouting(db, tokenConfig)
}
