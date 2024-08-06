package au.com.cding21.security.hashing

interface HashingService {
    fun generateSaltedHash(
        value: String,
        saltLength: Int = 32,
    ): SaltedHash

    fun verify(
        value: String,
        saltedHash: SaltedHash,
    ): Boolean
}
