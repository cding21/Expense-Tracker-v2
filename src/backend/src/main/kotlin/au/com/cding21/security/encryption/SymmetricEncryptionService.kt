package au.com.cding21.security.encryption

interface SymmetricEncryptionService {
    fun encrypt(key: ByteArray, plaintext: String, iv: ByteArray): ByteArray

    fun decrypt(encrypted: ByteArray, key: ByteArray, iv: ByteArray): String
}