package au.com.cding21.data

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

enum class SortBy {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY,
}

@Serializable
data class WeekPeriod(
    val start: LocalDate,
    val end: LocalDate,
)
