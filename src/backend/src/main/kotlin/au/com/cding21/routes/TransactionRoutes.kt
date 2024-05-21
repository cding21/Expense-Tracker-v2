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
    // Read transaction
    get("/transactions/{id}") {
        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
        transactionService.getTransactionById(id)?.let { transaction ->
            call.respond(transaction)
        } ?: call.respond(HttpStatusCode.NotFound)
    }
    // Update transaction
    put("/transactions/{id}") {
        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
        val transaction = call.receive<Transaction>()
        transactionService.updateTransaction(id, transaction)?.let {
            call.respond(HttpStatusCode.OK, it)
        } ?: call.respond(HttpStatusCode.NotFound)
    }
    // Delete transaction
    delete("/transactions/{id}") {
        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
        transactionService.deleteTransaction(id)?.let {
            call.respond(HttpStatusCode.OK, it)
        } ?: call.respond(HttpStatusCode.NotFound)
    }
}
