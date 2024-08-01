package au.com.cding21.data

import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId

data class User
    @BsonCreator
    constructor(
        @BsonProperty("username")
        val username: String,
        @BsonProperty("password")
        val password: String,
        @BsonProperty("salt")
        val salt: String,
        @BsonId
        val id: ObjectId = ObjectId.get(),
    )
