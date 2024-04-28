package au.com.cding21.routes

import au.com.cding21.model.Transaction
import au.com.cding21.plugins.Car
import au.com.cding21.services.TransactionService
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.transactionRoutes(
    transactionService: TransactionService
) {
    // Create transaction
    post("/transactions") {
        val transaction = call.receive<Transaction>()
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
            call.respond(HttpStatusCode.OK)
        } ?: call.respond(HttpStatusCode.NotFound)
    }
    // Delete transaction
    delete("/transactions/{id}") {
        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
        transactionService.deleteTransaction(id)?.let {
            call.respond(HttpStatusCode.OK)
        } ?: call.respond(HttpStatusCode.NotFound)
    }
}

/**
 * Establishes connection with a MongoDB database.
 *
 * The following configuration properties (in application.yaml/application.conf) can be specified:
 * * `db.mongo.user` username for your database
 * * `db.mongo.password` password for the user
 * * `db.mongo.host` host that will be used for the database connection
 * * `db.mongo.maxPoolSize` maximum number of connections to a MongoDB server
 * * `db.mongo.database.name` name of the database
 *
 * IMPORTANT NOTE: in order to make MongoDB connection working, you have to start a MongoDB server first.
 * See the instructions here: https://www.mongodb.com/docs/manual/administration/install-community/
 * all the paramaters above
 *
 * @returns [MongoDatabase] instance
 * */
fun Application.connectToMongoDB(): MongoDatabase {
    val user = environment.config.tryGetString("db.mongo.user")
    val password = environment.config.tryGetString("db.mongo.password")
    val host = environment.config.tryGetString("db.mongo.host") ?: "127.0.0.1"
    val maxPoolSize = environment.config.tryGetString("db.mongo.maxPoolSize")?.toInt() ?: 20
    val databaseName = environment.config.tryGetString("db.mongo.database.name") ?: "myDatabase"

    val credentials = user?.let { userVal -> password?.let { passwordVal -> "$userVal:$passwordVal@" } }.orEmpty()
    val uri = "mongodb+srv://$credentials$host/?maxPoolSize=$maxPoolSize&w=majority"

    val mongoClient = MongoClients.create(uri)
    val database = mongoClient.getDatabase(databaseName)

    environment.monitor.subscribe(ApplicationStopped) {
        mongoClient.close()
    }

    return database
}

