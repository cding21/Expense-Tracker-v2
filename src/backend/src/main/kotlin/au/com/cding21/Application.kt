package au.com.cding21

import au.com.cding21.plugins.*
import io.ktor.server.application.*


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSecurity()
    configureSerialization()
    configureDatabases()
    configureHTTP()
    configureRouting()

}
