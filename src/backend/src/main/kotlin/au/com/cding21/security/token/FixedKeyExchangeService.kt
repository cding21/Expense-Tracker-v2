package au.com.cding21.security.token

/**
 * Service to securely exchange fixed (e.g API) keys. Prevents session-replay based attack using intercepted keys
 */
interface FixedKeyExchangeService {
    /**
     * Accepts raw key as string, outputs a base64 encoded AES encryption with a time-based token as IV
     */
    fun encrypt(apiKey: String): Pair<String, String>

    /**
     * Accepts base64 encoded encryption, verify timeToken hasn't expired and decrypts using timeToken as IV
     */
    fun decrypt(encrypted: String, timeToken: String): String
}