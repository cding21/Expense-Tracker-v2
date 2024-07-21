package au.com.cding21.third_party.banks.types

import au.com.cding21.third_party.banks.util.md5Hash
import au.com.cding21.third_party.banks.util.throwIfNullKey
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.*

data class Transaction(
    val id: String,
    val date: LocalDateTime,
    val payee: String?,
    val payer: String?,
    val amount: Double,
    val currency: CurrencyCode,
    val description: String
) {
    companion object {
        /**
         * Static factory method that parses an ANZ JSON object to Transaction type
         */

        fun fromANZJson(jsonObject: JsonObject): Transaction {
            val isCredit = jsonObject.throwIfNullKey("transactionAmountType").jsonObject.throwIfNullKey("codeDescription").jsonPrimitive.content == "Credit"

            return Transaction(
                (jsonObject.throwIfNullKey("transactionDate").jsonPrimitive.content + jsonObject.throwIfNullKey("transactionRemarks").jsonPrimitive.content).md5Hash(),
                LocalDateTime.parse(jsonObject.throwIfNullKey("transactionDate").jsonPrimitive.content),
                jsonObject["payee"]?.jsonPrimitive?.content,
                jsonObject["payer"]?.jsonPrimitive?.content,
                jsonObject.throwIfNullKey("transactionAmount").jsonObject.throwIfNullKey("amount").jsonPrimitive.double * (if (isCredit) 1.0 else -1.0),
                CurrencyCode.fromString(jsonObject.throwIfNullKey("transactionAmount").jsonObject.throwIfNullKey("currency").jsonPrimitive.content),
                jsonObject.throwIfNullKey("transactionRemarks").jsonPrimitive.content
            )
        }
    }
}
