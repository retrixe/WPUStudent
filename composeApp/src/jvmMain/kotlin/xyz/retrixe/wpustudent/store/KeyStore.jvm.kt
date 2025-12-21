package xyz.retrixe.wpustudent.store

import com.github.javakeyring.Keyring
import com.github.javakeyring.PasswordAccessException
import io.ktor.util.decodeBase64Bytes
import io.ktor.util.encodeBase64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

private const val SERVICE = "xyz.retrixe.wpustudent"
private const val BASE_CIPHER = "AES"
private const val CIPHER_SIZE = 128
private const val CIPHER_ALGORITHM = "$BASE_CIPHER/GCM/NoPadding"
private val CHARSET = Charsets.UTF_8

private val keyring by lazy { Keyring.create() }
private val keyGenerator by lazy { KeyGenerator.getInstance(BASE_CIPHER) }

private fun generateSecretKey(keyAlias: String): SecretKey {
    try {
        val encodedKey = keyring.getPassword("xyz.retrixe.wpustudent", keyAlias)
            ?: throw PasswordAccessException("No such key alias \"$keyAlias\" stored")
        val decodedKey = encodedKey.decodeBase64Bytes()
        return SecretKeySpec(decodedKey, 0, decodedKey.size, BASE_CIPHER)
    } catch (_: PasswordAccessException) {
        val newKey = keyGenerator.apply { init(CIPHER_SIZE) }.generateKey()
        keyring.setPassword(SERVICE, keyAlias, newKey.encoded.encodeBase64())
        return newKey
    }
}

private fun getSecretKey(keyAlias: String): SecretKey? {
    try {
        val encodedKey = keyring.getPassword("xyz.retrixe.wpustudent", keyAlias)
            ?: throw PasswordAccessException("No such key alias \"$keyAlias\" stored")
        val decodedKey = encodedKey.decodeBase64Bytes()
        return SecretKeySpec(decodedKey, 0, decodedKey.size, BASE_CIPHER)
    } catch (_: PasswordAccessException) {
        return null
    }
}

actual fun encryptData(keyAlias: String, text: String): Pair<ByteArray, ByteArray> {
    val secretKey = generateSecretKey(keyAlias)
    val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    val encryptedData = cipher.doFinal(text.toByteArray(CHARSET))
    val iv = cipher.iv
    return Pair(iv, encryptedData)
}

actual fun decryptData(keyAlias: String, iv: ByteArray, encryptedData: ByteArray): String? {
    val secretKey = getSecretKey(keyAlias) ?: return null
    val gcmParameterSpec = GCMParameterSpec(CIPHER_SIZE, iv)
    val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
    cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec)
    return cipher.doFinal(encryptedData).toString(CHARSET)
}
