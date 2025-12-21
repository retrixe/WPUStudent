package xyz.retrixe.wpustudent.store

expect fun encryptData(keyAlias: String, text: String): Pair<ByteArray, ByteArray>

expect fun decryptData(keyAlias: String, iv: ByteArray, encryptedData: ByteArray): String?

fun encryptToString(keyAlias: String, text: String): String {
    val (iv, encryptedData) = encryptData(keyAlias, text)
    val ivString = iv.joinToString("") { it.toHexString() }
    val encryptedDataString = encryptedData.joinToString("") { it.toHexString() }
    return ivString + encryptedDataString
}

fun decryptFromString(keyAlias: String, encryptedText: String): String? {
    val iv =
        encryptedText.take(24).chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    val encryptedData =
        encryptedText.substring(24).chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    return decryptData(keyAlias, iv, encryptedData)
}
