package au.com.cding21.security.encryption

interface AsymmetricEncryptionService {
    fun getPubKey(): String

    fun encrypt(plaintext: String, foreignPubKey: String): Pair<ByteArray, ByteArray>

    fun decrypt(ciphertext: ByteArray, signature: ByteArray, foreignPubKey: String): String
}