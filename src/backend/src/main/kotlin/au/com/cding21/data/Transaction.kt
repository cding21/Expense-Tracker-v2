package au.com.cding21.data

import kotlinx.serialization.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import org.bson.Document
import org.bson.types.ObjectId
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
data class Transaction(
    val id: String,
    val userId: String,
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,
    val amount: Double,
    val currencyCode: String,
    val description: String,
    val category: String,
    val fromAccount: String?,
    val fromNote: String?,
    val toAccount: String?,
    val toNote: String?
) {
    fun toDocument(): Document = Document.parse(Json.encodeToString(this))

    companion object {
        private val json = Json { ignoreUnknownKeys = true }
        fun fromDocument(document: Document): Transaction = json.decodeFromString(document.toJson())
        /**
         * Static factory method that parses a CSV line to Transaction type. Due to no currency code being used in
         * ANZ's CSV export, the default currency code is set to "AUD"
         */
        fun fromCsvLine(line: String, userId: String): Transaction {
            val cleanedLine = line.replace("\"", "").replace("\\s+".toRegex(), " ")
            val values = cleanedLine.split(",\\s*".toRegex()).toTypedArray()
            return Transaction(
                ObjectId().toString(),
                userId,
                LocalDate.parse(values[0], DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                values[1].toDouble(),
                "AUD", // Default currency code to "AUD
                values[2],
                values[3],
                values[4],
                values[5],
                values[6],
                values[7]
            )
        }
    }
}


@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LocalDate::class)
object LocalDateSerializer : KSerializer<LocalDate> {
    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString(), formatter)
    }
}