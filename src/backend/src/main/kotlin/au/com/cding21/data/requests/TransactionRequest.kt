package au.com.cding21.data.requests

import au.com.cding21.data.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class TransactionRequest(
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,
    val amount: Double,
    val currencyCode: String,
    val description: String,
    val category: String,
    val fromAccount: String,
    val fromNote: String,
    val toAccount: String,
    val toNote: String
)
