package com.example.encryption

import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

class Presenter {

    var encodedBytes: ByteArray? = null
    var decodedBytes: ByteArray? = null
    var secretKeySpec: SecretKeySpec? = null

    fun generateKey() {
        try {
            val sr: SecureRandom = SecureRandom.getInstance("SHA1PRNG")
            sr.setSeed("any data used as random seed".toByteArray())
            val kg: KeyGenerator = KeyGenerator.getInstance("AES")
            kg.init(192, sr)
            secretKeySpec = SecretKeySpec(kg.generateKey().encoded, "AES")
        } catch (e: Exception) {
            Log.e("Crypto", "AES secret key spec error")
        }
    }

    fun decrypt(encryptedText: String): ByteArray? {
        try {
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
            val bytes = encryptedText.toByteArray(Charsets.UTF_8)
            decodedBytes = cipher.doFinal(Base64.decode(bytes, Base64.DEFAULT))
        } catch (e: Exception) {
            Log.e("Crypto", e.message.toString())
        }
        return decodedBytes
    }

    fun encrypt(testText: String) {
        try {
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
            encodedBytes = cipher.doFinal(testText.toByteArray())
        } catch (e: Exception) {
            Log.e("Crypto", "AES encryption error")
        }

    }

    fun getEncryptedText(): String {
        return String(Base64.encode(encodedBytes, Base64.DEFAULT))
    }


    fun getDecryptedText(): String? {
        return decodedBytes?.let { String(it) }
    }


    fun getKeyText(): String {
        return secretKeySpec?.let { String(it.encoded) }.toString()
    }
}