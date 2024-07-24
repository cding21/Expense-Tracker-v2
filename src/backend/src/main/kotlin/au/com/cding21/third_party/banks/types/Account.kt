package au.com.cding21.third_party.banks.types

import au.com.cding21.third_party.banks.util.throwIfNullKey
import kotlinx.serialization.json.*

data class Account(
    val id: String,
    val name: String,
    val currencyType: CurrencyCode,
    val currentBalance: Double,
    val availableBalance: Double,
) {
    companion object {
        /**
         * Static factory method that parses an ANZ JSON object to Account type
         */
        fun fromANZJson(jsonObject: JsonObject): Account {
            val balancesArray = jsonObject.throwIfNullKey("balances").jsonArray

            return Account(
                "${jsonObject.throwIfNullKey("branchCode").jsonPrimitive.content} ${jsonObject.throwIfNullKey("accountId").jsonPrimitive.content}",
                jsonObject.throwIfNullKey("accountName").jsonPrimitive.content,
                CurrencyCode.fromString(jsonObject.throwIfNullKey("accountCurrency").jsonObject.throwIfNullKey("cmCode").jsonPrimitive.content),
                balancesArray.filter { it.jsonObject.throwIfNullKey("type").jsonPrimitive.content == "Current Balance" }[0].jsonObject.throwIfNullKey("amountDetails").jsonObject.throwIfNullKey("amount").jsonPrimitive.content.toDouble(),
                balancesArray.filter { it.jsonObject.throwIfNullKey("type").jsonPrimitive.content == "Available Balance" }[0].jsonObject.throwIfNullKey("amountDetails").jsonObject.throwIfNullKey("amount").jsonPrimitive.content.toDouble(),
                )
        }

        fun fromINGJson(jsonObject: JsonObject): Account {
            return Account(
                "${jsonObject.throwIfNullKey("BSB").jsonPrimitive.content} ${jsonObject.throwIfNullKey("AccountNumber").jsonPrimitive.content}",
                jsonObject.throwIfNullKey("AccountName").jsonPrimitive.content,
                CurrencyCode.AUD,
                jsonObject.throwIfNullKey("CurrentBalance").jsonPrimitive.double,
                jsonObject.throwIfNullKey("AvailableBalance").jsonPrimitive.double,
            )
        }
    }
}
