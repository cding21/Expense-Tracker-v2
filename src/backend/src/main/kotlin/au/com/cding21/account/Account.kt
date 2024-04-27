package au.com.cding21.account

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.Document

@Serializable
data class Account(
    val name: String,
    val balance: Double,
    val transactions: List<Transaction>
) {

    fun toDocument(): Document = Document.parse(Json.encodeToString(this))

    companion object {
        private val json = Json { ignoreUnknownKeys = true }
        fun fromDocument(document: Document): Transaction = json.decodeFromString(document.toJson())
    }
}


