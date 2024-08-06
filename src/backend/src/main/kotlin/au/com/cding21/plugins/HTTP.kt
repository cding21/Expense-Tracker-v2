package au.com.cding21.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.request.*
import org.slf4j.event.Level

fun Application.configureHTTP() {
    val allowedWebsite = environment.config.property("ktor.cors.staticWebsite").getString()
    val allowedDashboard = environment.config.property("ktor.cors.dashboard").getString()
    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowCredentials = true
        allowHost(allowedWebsite)
        allowHost(allowedDashboard)
    }
    install(CallLogging) {
        level = Level.INFO
        filter { call ->
            call.request.path().startsWith(System.getenv("API_VERSION") ?: "/api/v0")
        }
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val userAgent = call.request.headers["User-Agent"]
            "Status: $status, HTTP method: $httpMethod, User agent: $userAgent"
        }
    }
}
