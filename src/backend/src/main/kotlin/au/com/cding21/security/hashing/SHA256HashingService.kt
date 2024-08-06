package au.com.cding21.security.hashing

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.security.SecureRandom

class SHA256HashingService : HashingService {
    override fun generateSaltedHash(
        value: String,
        saltLength: Int,
    ): SaltedHash {
        val salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLength)
        val saltAsHex = Hex.encodeHexString(salt)
        val hash = DigestUtils.sha256Hex(value + saltAsHex)
        return SaltedHash(hash, saltAsHex)
    }

    override fun verify(
        value: String,
        saltedHash: SaltedHash,
    ): Boolean {
        return DigestUtils.sha256Hex(value + saltedHash.salt) == saltedHash.hash
    }
}
