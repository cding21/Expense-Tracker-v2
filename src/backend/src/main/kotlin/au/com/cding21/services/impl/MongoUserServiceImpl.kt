package au.com.cding21.services.impl

import au.com.cding21.data.User
import au.com.cding21.services.UserService
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.litote.kmongo.*

class MongoUserServiceImpl(
    private val db: MongoDatabase
): UserService {
    private val users: MongoCollection<User>

    init {
        db.createCollection("users")
        users = db.getCollection<User>("users")
    }

    override suspend fun getUserByUsername(username: String): User? {
        return users.findOne(User::username eq username)
    }

    override suspend fun createUser(user: User): Boolean {
        return users.insertOne(user).wasAcknowledged()
    }
}