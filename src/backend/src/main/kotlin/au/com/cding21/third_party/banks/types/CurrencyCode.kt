package au.com.cding21.third_party.banks.types

// TODO: Add more currency codes
enum class CurrencyCode {
    AUD,
    USD,
    ;

    companion object {
        fun fromString(string: String): CurrencyCode =
            when (string) {
                "AUD" -> AUD
                "USD" -> USD
                else -> throw Exception("CurrencyCode: Currency code string does not match predefined currency codes")
            }
    }
}
