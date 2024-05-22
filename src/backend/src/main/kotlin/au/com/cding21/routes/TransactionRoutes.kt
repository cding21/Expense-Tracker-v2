package au.com.cding21.routes

import au.com.cding21.data.Transaction
import au.com.cding21.data.requests.TransactionRequest
import au.com.cding21.services.impl.MongoTransactionServiceImpl
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


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
                    call.respond(HttpStatusCode.Forbidden)
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
                    call.respond(HttpStatusCode.Forbidden)
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
                    call.respond(HttpStatusCode.Forbidden)
                    return@delete
                }
            }
        }?.let {
            call.respond(HttpStatusCode.OK, it)
        } ?: call.respond(HttpStatusCode.NotFound)
    }
}
