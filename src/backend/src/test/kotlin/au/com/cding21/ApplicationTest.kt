package au.com.cding21

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import org.junit.Assert.assertEquals
import org.junit.Test

class ApplicationTest {
    @Test
    fun testRoot() =
        testApplication {
            environment {
                config =
                    MapApplicationConfig().apply {
                        put("API_VERSION", "/api/v1")

                        put("DB_TYPE", "mongo")
                        put("DB_MONGO_USER", "testUser")
                        put("DB_MONGO_PASSWORD", "testPassword")
                        put("DB_MONGO_HOST", "localhost")
                        put("DB_MONGO_PORT", "27017")

                        put("jwt.issuer", "test.com.au")
                        put("jwt.audience", "test.com.au")
                        put("jwt.expiresIn", "10000")
                        put("jwt.secret", "testSecret")
                    }
            }
            System.setProperty("API_VERSION", "/api/v1")
            routing {
                route(System.getProperty("API_VERSION") ?: "/api/v0") {
                    get("") {
                        call.respondText("Hello World!")
                    }
                }
            }
            client.get(System.getProperty("API_VERSION") ?: "/api/v0").apply {
                assertEquals(HttpStatusCode.OK, status)
                assertEquals("Hello World!", bodyAsText())
            }
        }
}
