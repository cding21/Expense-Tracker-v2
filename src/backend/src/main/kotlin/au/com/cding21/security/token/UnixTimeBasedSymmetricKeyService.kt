package au.com.cding21.security.token

import au.com.cding21.security.encryption.SymmetricEncryptionService
import java.util.*

/**
 * Uses unix time as a timeToken. Ensures symmetric encryption is dynamic and prevents stale tokens from being used
 */
class UnixTimeBasedSymmetricKeyService(private val encryptor: SymmetricEncryptionService) : FixedKeyExchangeService {
    private val SECRET_KEY = System.getenv("SECRET_KEY").toByteArray()

    fun padIV(key: ByteArray): ByteArray {
        return key + generateSequence(0) { it }.take(16 - key.size).joinToString("").toByteArray()
    }

    override fun encrypt(apiKey: String): Pair<String, String> {
        val unixTime = System.currentTimeMillis().toString()
        val encrypted = encryptor.encrypt(SECRET_KEY, apiKey, padIV(unixTime.toByteArray()))
        return Pair(Base64.getEncoder().encodeToString(encrypted), unixTime)
    }

    override fun decrypt(encrypted: String, timeToken: String): String {
        return encryptor.decrypt(Base64.getDecoder().decode(encrypted), SECRET_KEY, padIV(timeToken.toByteArray()))
    }
}