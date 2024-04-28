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


class MongoTransactionServiceImpl (
    private val db: MongoDatabase
): TransactionService {
    private var transactions: MongoCollection<Document>

    init {
        db.createCollection("transactions")
        transactions = db.getCollection("transactions")
    }

    override suspend fun createTransaction(transaction: Transaction): String = withContext(Dispatchers.IO) {
        val doc = transaction.toDocument()
        transactions.insertOne(doc)
        doc["_id"].toString()
    }

    override suspend fun getTransactionByUserId(userId: String): List<Transaction> = withContext(Dispatchers.IO) {
        transactions.find(Filters.eq("userId", userId)).toList().map { doc ->
            Transaction.fromDocument(doc)
        }
    }

    override suspend fun getTransactionById(id: String): Transaction? = withContext(Dispatchers.IO) {
        transactions.findOneById(ObjectId(id))?.let(Transaction::fromDocument)
    }

    override suspend fun updateTransaction(id: String, transaction: Transaction): Transaction? = withContext(Dispatchers.IO) {
        transactions.findOneAndReplace(Filters.eq("_id", ObjectId(id)), transaction.toDocument())
            ?.let { Transaction.fromDocument(it) }
    }

    override suspend fun deleteTransaction(id: String): Transaction? = withContext(Dispatchers.IO) {
        transactions.findOneAndDelete(Filters.eq("_id", ObjectId(id)))
            ?.let { Transaction.fromDocument(it) }
    }
}







