package au.com.cding21.account

import au.com.cding21.plugins.Car
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.Document
import org.bson.types.ObjectId

class AccountService (private val database: MongoDatabase) {
    var collection: MongoCollection<Document>

    init {
        database.createCollection("account")
        database.createCollection("transactions")
        collection = database.getCollection("account")
    }

    // Create new car
    suspend fun create(account: Account): String = withContext(Dispatchers.IO) {
        val doc = account.toDocument()
        collection.insertOne(doc)
        doc["_id"].toString()
    }

    // Read a car
    suspend fun read(id: String): Car? = withContext(Dispatchers.IO) {
        collection.find(Filters.eq("_id", ObjectId(id))).first()?.let(Car::fromDocument)
    }

    // Update a car
    suspend fun update(id: String, car: Car): Document? = withContext(Dispatchers.IO) {
        collection.findOneAndReplace(Filters.eq("_id", ObjectId(id)), car.toDocument())
    }

    // Delete a car
    suspend fun delete(id: String): Document? = withContext(Dispatchers.IO) {
        collection.findOneAndDelete(Filters.eq("_id", ObjectId(id)))
    }
}
