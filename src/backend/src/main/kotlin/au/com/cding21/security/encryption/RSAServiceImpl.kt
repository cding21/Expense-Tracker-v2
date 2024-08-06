package au.com.cding21.security.encryption

import org.bouncycastle.crypto.digests.SHA512Digest
import org.bouncycastle.crypto.engines.RSAEngine
import org.bouncycastle.crypto.signers.RSADigestSigner
import org.bouncycastle.crypto.util.OpenSSHPrivateKeyUtil
import org.bouncycastle.crypto.util.OpenSSHPublicKeyUtil
import org.bouncycastle.util.io.pem.PemReader
import java.io.StringReader
import java.security.SignatureException
import java.util.Base64

class RSAServiceImpl(private val pubKey: String, private val privateKey: String): AsymmetricEncryptionService {
    override fun getPubKey(): String {
        return pubKey
    }

    override fun encrypt(plaintext: String, foreignPubKey: String): Pair<ByteArray, ByteArray> {
        var formattedPrivateKey = privateKey.replace(" ", "\n")
        formattedPrivateKey = formattedPrivateKey.replace("-----BEGIN\n" +
                "OPENSSH\n" +
                "PRIVATE\n" +
                "KEY-----", "-----BEGIN OPENSSH PRIVATE KEY-----")
        formattedPrivateKey = formattedPrivateKey.replace("-----END\n" +
                "OPENSSH\n" +
                "PRIVATE\n" +
                "KEY-----", "-----END OPENSSH PRIVATE KEY-----")
        val pemReader = PemReader(StringReader(formattedPrivateKey))
        val parsedPrivateKey = OpenSSHPrivateKeyUtil.parsePrivateKeyBlob(pemReader.readPemObject().content)

        val bytes = plaintext.toByteArray()
        val engine = RSAEngine()
        engine.init(true, OpenSSHPublicKeyUtil.parsePublicKey(Base64.getDecoder().decode(foreignPubKey)))
        val rawBytes = engine.processBlock(bytes, 0, bytes.size)

        val signer = RSADigestSigner(SHA512Digest())
        signer.init(true, parsedPrivateKey)
        signer.update(rawBytes, 0, rawBytes.size)
        return Pair(rawBytes, signer.generateSignature())
    }

    override fun decrypt(ciphertext: ByteArray, signature: ByteArray, foreignPubKey: String): String {
        var formattedPrivateKey = privateKey.replace(" ", "\n")
        formattedPrivateKey = formattedPrivateKey.replace("-----BEGIN\n" +
                "OPENSSH\n" +
                "PRIVATE\n" +
                "KEY-----", "-----BEGIN OPENSSH PRIVATE KEY-----")
        formattedPrivateKey = formattedPrivateKey.replace("-----END\n" +
                "OPENSSH\n" +
                "PRIVATE\n" +
                "KEY-----", "-----END OPENSSH PRIVATE KEY-----")
        val pemReader = PemReader(StringReader(formattedPrivateKey))
        val parsedPrivateKey = OpenSSHPrivateKeyUtil.parsePrivateKeyBlob(pemReader.readPemObject().content)

        val verifier = RSADigestSigner(SHA512Digest())
        verifier.init(false, OpenSSHPublicKeyUtil.parsePublicKey(Base64.getDecoder().decode(foreignPubKey)))
        verifier.update(ciphertext, 0, ciphertext.size)
        if (!verifier.verifySignature(signature)) {
            throw SignatureException("Invalid signature")
        }

        val engine = RSAEngine()
        engine.init(false, parsedPrivateKey)
        val plaintext = engine.processBlock(ciphertext, 0, ciphertext.size)
        return plaintext.decodeToString()
    }
}