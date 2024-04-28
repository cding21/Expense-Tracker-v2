package au.com.cding21.util

import au.com.cding21.data.User
import com.mongodb.MongoClientSettings
import org.bson.*
import org.bson.codecs.Codec
import org.bson.codecs.CollectibleCodec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext

class UserCodec: CollectibleCodec<User> {
    private val userCodec: Codec<Document> = MongoClientSettings.getDefaultCodecRegistry().get(Document::class.java)

    override fun encode(w: BsonWriter?, user: User?, context: EncoderContext?) {
        val doc = Document()
        doc["username"] = user?.username
        doc["password"] = user?.password
        doc["salt"] = user?.salt
        userCodec.encode(w, doc, context)
    }

    override fun generateIdIfAbsentFromDocument(document: User): User {
        return document
    }

    override fun documentHasId(document: User): Boolean {
        return true
    }

    override fun getDocumentId(document: User): BsonValue {
        return BsonString(document.id.toString())
    }

    override fun getEncoderClass(): Class<User> {
        return User::class.java
    }

    override fun decode(reader: BsonReader, decoderContext: DecoderContext): User {
        val document = userCodec.decode(reader, decoderContext)
        val user = User(
            document.getString("username"),
            document.getString("password"),
            document.getString("salt"),
            document.getObjectId("_id")
        )
        return user
    }
}