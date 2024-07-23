package au.com.cding21.services.impl

import au.com.cding21.data.Transaction
import au.com.cding21.services.TransactionService
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.Document
import org.bson.types.ObjectId
import org.litote.kmongo.*
import java.io.BufferedReader
import java.util.stream.Collectors


class MongoTransactionServiceImpl (
    db: MongoDatabase
): TransactionService {
    private var transactions: MongoCollection<Document>

    init {
        db.createCollection("transactions")
        transactions = db.getCollection("transactions")
    }

    override suspend fun createTransaction(transaction: Transaction): String = withContext(Dispatchers.IO) {
        val doc = transaction.toDocument()
        transactions.insertOne(doc)
        doc["id"].toString()
    }

    override suspend fun getTransactionByUserId(userId: String): List<Transaction> = withContext(Dispatchers.IO) {
        transactions.find(Filters.eq("userId", userId)).toList().map { doc ->
            Transaction.fromDocument(doc)
        }
    }

    override suspend fun getTransactionById(id: String): Transaction? = withContext(Dispatchers.IO) {
        transactions.findOne(Filters.eq("id", id))?.let(Transaction::fromDocument)
    }

    override suspend fun updateTransaction(id: String, transaction: Transaction): Transaction? = withContext(Dispatchers.IO) {
        transactions.findOneAndReplace(Filters.eq("id", id), transaction.toDocument())
            ?.let { Transaction.fromDocument(it) }
    }

    override suspend fun deleteTransaction(id: String): Transaction? = withContext(Dispatchers.IO) {
        transactions.findOneAndDelete(Filters.eq("id", id))
            ?.let { Transaction.fromDocument(it) }
    }

    override suspend fun uploadTransactions(csv: BufferedReader, userId: String): List<Transaction> {
        return csv.lines()
            .filter { it.isNotBlank() }
            .map {
            val t = Transaction.fromCsvLine(it, userId)
//            transactions.insertOne(t.toDocument())
            t
        }.collect(Collectors.toList())
    }
}







