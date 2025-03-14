package au.com.cding21.routes

import au.com.cding21.data.SortBy
import au.com.cding21.data.Transaction
import au.com.cding21.data.TransactionStat
import au.com.cding21.data.WeekPeriod
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
import io.ktor.util.*
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import org.bson.types.ObjectId
import java.time.temporal.WeekFields
import java.util.*

fun Route.transactionRoutes(transactionService: MongoTransactionServiceImpl) {
    // Create transaction
    post("/transactions") {
        val principal = call.principal<JWTPrincipal>()
        val userId = principal!!.payload.getClaim("userId").asString()
        val transactionReq = call.receive<TransactionRequest>()
        val transaction =
            Transaction(
                ObjectId().toString(), // Generate a new ObjectId
                userId,
                transactionReq.date,
                transactionReq.amount,
                transactionReq.currencyCode,
                transactionReq.description,
                transactionReq.category,
                transactionReq.tags,
                transactionReq.fromAccount,
                transactionReq.fromNote,
                transactionReq.toAccount,
                transactionReq.toNote,
            )
        println("transaction: $transaction")
        val id = transactionService.createTransaction(transaction)
        call.respond(HttpStatusCode.Created, id)
    }
    // Read all transactions for a user
    get("/transactions") {
        val principal = call.principal<JWTPrincipal>()
        val userId = principal!!.payload.getClaim("userId").asString()
        val transactions = transactionService.getTransactionByUserId(userId)
        val mode =
            call.request.queryParameters["sortBy"]?.let {
                    it1 ->
                enumValueOf<SortBy>(it1.toUpperCasePreservingASCIIRules())
            }
        when (mode) {
            SortBy.YEARLY -> {
                val response = HashMap<Int, MutableList<Transaction>>()
                // Group transactions by year
                call.respond(transactions.groupByTo(response) { it.date.year })
            }
            SortBy.MONTHLY -> {
                val response = HashMap<Int, MutableList<Transaction>>()
                // Group transactions by year and then by month
                call.respond(
                    transactions.groupByTo(response) { it.date.year }.mapValues {
                            (_, transactions) ->
                        val t = HashMap<Month, MutableList<Transaction>>()
                        transactions.groupByTo(t) { it.date.month }
                    },
                )
            }
            SortBy.WEEKLY -> {
                val response = HashMap<Int, MutableList<Transaction>>()
                val weekFields = WeekFields.of(Locale.getDefault())
                // Group transactions by year and then by month
                call.respond(
                    transactions.groupByTo(response) { it.date.year }.mapValues {
                            (_, transactions) ->
                        val t = HashMap<WeekPeriod, MutableList<Transaction>>()
                        transactions.groupByTo(t) { transaction ->
                            val startOfWeek = transaction.date.with(weekFields.dayOfWeek(), 1).toKotlinLocalDate()
                            val endOfWeek = transaction.date.with(weekFields.dayOfWeek(), 7).toKotlinLocalDate()
                            WeekPeriod(startOfWeek, endOfWeek)
                        }
                    },
                )
            }
            SortBy.DAILY -> {
                val response = HashMap<LocalDate, MutableList<Transaction>>()
                // Group transactions by date
                call.respond(transactions.groupByTo(response) { it.date.toKotlinLocalDate() })
            }
            else -> call.respond(transactions)
        }
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
    // Read transaction summary
    get("/transactions/summary") {
        val principal = call.principal<JWTPrincipal>()

        val userId = principal!!.payload.getClaim("userId").asString()

        val transactions = transactionService.getTransactionByUserId(userId)

        val sortedTransactions = HashMap<Int, MutableList<Transaction>>()

        // Group transactions by year and then by month
        val data =
            transactions.groupByTo(sortedTransactions) { it.date.year }.mapValues {
                    (_, transactions) ->
                val t = HashMap<Month, MutableList<Transaction>>()
                transactions.groupByTo(t) { it.date.month }
            }

        // TODO: Could change this to only track expenses up until a certain day for similiar comparisons between
        // months

        // Calculate the sum of income in for this month
        val currentMonth = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).month
        val currentYear = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
        val currentMonthTransactions = data[currentYear]?.get(currentMonth)
        val lastMonthTransactions = data[currentYear]?.get(currentMonth.minus(1))

        val currentMonthIncome = currentMonthTransactions?.filter { it.amount > 0 }?.sumOf { it.amount }
        val lastMonthIncome = lastMonthTransactions?.filter { it.amount > 0 }?.sumOf { it.amount }
        val diffPercentageIncome = ((currentMonthIncome ?: 0.0) - (lastMonthIncome ?: 0.0)) / (lastMonthIncome ?: 1.0) * 100

        val currentMonthExpenses = currentMonthTransactions?.filter { it.amount < 0 }?.sumOf { it.amount }
        val lastMonthExpenses = lastMonthTransactions?.filter { it.amount < 0 }?.sumOf { it.amount }
        val diffPercentageExpenses = ((currentMonthExpenses ?: 0.0) - (lastMonthExpenses ?: 0.0)) / (lastMonthExpenses ?: 1.0) * 100

        val currentMonthNet = currentMonthIncome?.plus(currentMonthExpenses ?: 0.0)
        val lastMonthNet = lastMonthIncome?.plus(lastMonthExpenses ?: 0.0)
        val diffPercentageNet = ((currentMonthNet ?: 0.0) - (lastMonthNet ?: 0.0)) / (lastMonthNet ?: 1.0) * 100

        call.respond(
            listOf(
                TransactionStat("0", "Money In", diffPercentageIncome, "pigMoney", currentMonthIncome ?: 0.0),
                TransactionStat("1", "Money Out", diffPercentageExpenses, "cash", currentMonthExpenses ?: 0.0),
                TransactionStat("2", "Net Change", diffPercentageNet, "report", currentMonthNet ?: 0.0),
            ),
        )
    }
    // Update transaction
    put("/transactions/{id}") {
        val principal = call.principal<JWTPrincipal>()

        val userId = principal!!.payload.getClaim("userId").asString()
        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
        val transactionReq = call.receive<TransactionRequest>()
        val transaction =
            Transaction(
                id,
                userId,
                transactionReq.date,
                transactionReq.amount,
                transactionReq.currencyCode,
                transactionReq.description,
                transactionReq.category,
                transactionReq.tags,
                transactionReq.fromAccount,
                transactionReq.fromNote,
                transactionReq.toAccount,
                transactionReq.toNote,
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
        println("id: $id")

        val transactionToDelete = transactionService.getTransactionById(id)
        if (transactionToDelete == null) {
            call.respond(HttpStatusCode.NotFound)
            return@delete
        }
        if (transactionToDelete.userId != userId) {
            call.respond(HttpStatusCode.Forbidden, "Unrelated user transaction")
            return@delete
        } else {
            transactionService.deleteTransaction(id)
            call.respond(HttpStatusCode.OK)
        }
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
