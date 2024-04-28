package au.com.cding21

import au.com.cding21.plugins.*
import au.com.cding21.routes.configureRouting
import io.ktor.server.application.*


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val db = connectToMongoDB()

    configureSecurity()
    configureSerialization()
    configureDatabases()
    configureHTTP()
    configureRouting(db)

}
