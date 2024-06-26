package au.com.cding21.routes

import au.com.cding21.data.Transaction
import au.com.cding21.data.requests.TransactionRequest
import au.com.cding21.services.impl.MongoTransactionServiceImpl
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import java.awt.TrayIcon.MessageType


fun Route.transactionRoutes(
    transactionService: MongoTransactionServiceImpl
) {
    // Create transaction
    post("/transactions") {
        val principal = call.principal<JWTPrincipal>()
        val userId = principal!!.payload.getClaim("userId").asString()
        val transactionReq = call.receive<TransactionRequest>()
        val transaction = Transaction(
            userId,
            transactionReq.date,
            transactionReq.amount,
            transactionReq.description,
            transactionReq.category,
            transactionReq.fromAccount,
            transactionReq.fromNote,
            transactionReq.toAccount,
            transactionReq.toNote
        )
        val id = transactionService.createTransaction(transaction)
        call.respond(HttpStatusCode.Created, id)
    }
    // Read all transactions for a user
    get("/transactions") {
        val principal = call.principal<JWTPrincipal>()
        val userId = principal!!.payload.getClaim("userId").asString()
        val transactions = transactionService.getTransactionByUserId(userId)
        call.respond(transactions)
    }
    // Read transaction
    get("/transactions/{id}") {
        val principal = call.principal<JWTPrincipal>()

        val userId = principal!!.payload.getClaim("userId").asString()
        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")

        transactionService.getTransactionById(id).also {
            it?.userId.let { transactionUserId ->
                if (transactionUserId != userId) {
                    call.respond(HttpStatusCode.Forbidden, "Unrelated user transaction")
                    return@get
                }
            }
        }?.let { transaction ->
            call.respond(transaction)
        } ?: call.respond(HttpStatusCode.NotFound)
    }
    // Update transaction
    put("/transactions/{id}") {
        val principal = call.principal<JWTPrincipal>()

        val userId = principal!!.payload.getClaim("userId").asString()
        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
        val transactionReq = call.receive<TransactionRequest>()
        val transaction = Transaction(
            userId,
            transactionReq.date,
            transactionReq.amount,
            transactionReq.description,
            transactionReq.category,
            transactionReq.fromAccount,
            transactionReq.fromNote,
            transactionReq.toAccount,
            transactionReq.toNote
        )

        transactionService.updateTransaction(id, transaction)?.also {
            it.userId.let { transactionUserId ->
                if (transactionUserId != userId) {
                    call.respond(HttpStatusCode.Forbidden, "Unrelated user transaction")
                    return@put
                }
            }
        }?.let {
            call.respond(HttpStatusCode.OK, it)
        } ?: call.respond(HttpStatusCode.NotFound)
    }
    // Delete transaction
    delete("/transactions/{id}") {
        val principal = call.principal<JWTPrincipal>()

        val userId = principal!!.payload.getClaim("userId").asString()
        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")

        transactionService.deleteTransaction(id)?.also {
            it.userId.let { transactionUserId ->
                if (transactionUserId != userId) {
                    call.respond(HttpStatusCode.Forbidden, "Unrelated user transaction")
                    return@delete
                }
            }
        }?.let {
            call.respond(HttpStatusCode.OK, it)
        } ?: call.respond(HttpStatusCode.NotFound)
    }
    // Upload transaction csv
    post("/transactions/upload") {
        call.request.headers.forEach { key, value ->
            println("$key: $value")
        }
        val principal = call.principal<JWTPrincipal>()
        val userId = principal!!.payload.getClaim("userId").asString()
        val multipart = call.receiveMultipart()
        val parts = multipart.readAllParts()
        val filePart = parts.filterIsInstance<PartData.FileItem>().first()
        val transactions = transactionService.uploadTransactions(filePart.streamProvider().bufferedReader(), userId)
        call.respond(transactions)
    }
}
