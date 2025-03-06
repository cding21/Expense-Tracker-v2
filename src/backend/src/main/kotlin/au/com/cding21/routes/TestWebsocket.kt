package au.com.cding21.routes

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*

fun Route.websocketRoutes() {
    webSocket("/ws") {
        send("Connected to websocket")
        for (frame in incoming) {
            frame as? Frame.Text ?: continue
            val receivedText = frame.readText()
            send("You sent: $receivedText")
        }
    }
}