package xyz.retrixe.wpustudent.store

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties.BLOCK_MODE_GCM
import android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE
import android.security.keystore.KeyProperties.KEY_ALGORITHM_AES
import android.security.keystore.KeyProperties.PURPOSE_DECRYPT
import android.security.keystore.KeyProperties.PURPOSE_ENCRYPT
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

// Credit: https://iamraghavawasthi.medium.com/securing-your-android-datastore-4c50f3e98d5c

private const val PROVIDER = "AndroidKeyStore"
private const val CIPHER_ALGORITHM = "AES/GCM/NoPadding"
private val CHARSET = Charsets.UTF_8

private val keyStore by lazy { KeyStore.getInstance(PROVIDER).apply { load(null) } }
private val keyGenerator by lazy { KeyGenerator.getInstance(KEY_ALGORITHM_AES, PROVIDER) }

private fun generateSecretKey(keyAlias: String): SecretKey {
    val keyEntry = keyStore.getEntry(keyAlias, null)
    return if (keyEntry == null) {
        keyGenerator.apply {
            init(
                KeyGenParameterSpec
                    .Builder(keyAlias, PURPOSE_ENCRYPT or PURPOSE_DECRYPT)
                    .setBlockModes(BLOCK_MODE_GCM)
                    .setEncryptionPaddings(ENCRYPTION_PADDING_NONE)
                    .build()
            )
        }.generateKey()
    } else {
        (keyEntry as KeyStore.SecretKeyEntry).secretKey
    }
}

private fun getSecretKey(keyAlias: String) =
    (keyStore.getEntry(keyAlias, null) as KeyStore.SecretKeyEntry?)?.secretKey

fun encryptData(keyAlias: String, text: String): Pair<ByteArray, ByteArray> {
    val secretKey = generateSecretKey(keyAlias)
    val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    val encryptedData = cipher.doFinal(text.toByteArray(CHARSET))
    val iv = cipher.iv
    return Pair(iv, encryptedData)
}

fun encryptToString(keyAlias: String, text: String): String {
    val (iv, encryptedData) = encryptData(keyAlias, text)
    val ivString = iv.joinToString("") { "%02x".format(it) }
    val encryptedDataString = encryptedData.joinToString("") { "%02x".format(it) }
    return ivString + encryptedDataString
}

fun decryptData(keyAlias: String, iv: ByteArray, encryptedData: ByteArray): String? {
    val secretKey = getSecretKey(keyAlias) ?: return null
    val gcmParameterSpec = GCMParameterSpec(128, iv)
    val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
    cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec)
    return cipher.doFinal(encryptedData).toString(CHARSET)
}

fun decryptFromString(keyAlias: String, encryptedText: String): String? {
    val iv =
        encryptedText.take(24).chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    val encryptedData =
        encryptedText.substring(24).chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    return decryptData(keyAlias, iv, encryptedData)
}
