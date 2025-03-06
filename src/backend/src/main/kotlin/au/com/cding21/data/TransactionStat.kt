package au.com.cding21.data

import kotlinx.serialization.Serializable

@Serializable
data class TransactionStat(
    val id: String,
    val title: String,
    val diff: Double,
    val icon: String,
    val value: Double,
)
