package au.com.cding21.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class GetIVRequest(
    val pubKey: String
)

@Serializable
data class getIVResponse(
    val encryptedString: String,
    val signature: String
)
