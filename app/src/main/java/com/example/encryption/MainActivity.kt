package com.example.encryption

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.encryption.ui.theme.EncryptionTheme
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class MainActivity : ComponentActivity() {

    lateinit var presenter: Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        presenter = Presenter()

        setContent {

            val originalText = rememberSaveable{ mutableStateOf("") }
            val encryptedText = rememberSaveable { mutableStateOf("") }
            val key = remember { mutableStateOf("") }

            EncryptionTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(verticalArrangement = Arrangement.Center ,horizontalAlignment = Alignment.CenterHorizontally) {
                        FieldWithCopy(originalText, clipboardManager)
                        FieldWithCopy(encryptedText, clipboardManager)
                        FieldWithCopy(key, clipboardManager)
                        Row {
                            Button(onClick = {
                                presenter.encrypt(originalText.value)
                                encryptedText.value = presenter.getEncryptedText()
                                presenter.encodedBytes = null
                            }) {
                                Text("Encrypt")
                            }
                            Button(onClick = {
                                presenter.decrypt(encryptedText.value)
                                originalText.value = presenter.getDecryptedText().toString()
                                presenter.decodedBytes = null
                            }) {
                                Text("Decrypt")
                            }
                            Button(onClick = {
                                presenter.generateKey()
                                key.value = presenter.getKeyText()
                            }) {
                                Text("Generate key")
                            }
                        }
                    }
                }
            }
        }
        presenter.generateKey()
    }
}

@Composable
fun FieldWithCopy(text: MutableState<String>, clipboardManager: ClipboardManager) {
    Row {
        TextField(modifier = Modifier.width(300.dp), value = text.value, onValueChange = {newText -> text.value = newText})
        IconButton(modifier = Modifier
            .height(30.dp)
            .width(20.dp) ,onClick = {
                val clip = ClipData.newPlainText("text label", text.value)
                clipboardManager.setPrimaryClip(clip)
        }) {
            Icon(painter = painterResource(id = R.drawable.ic_copy), contentDescription = "Copy")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    EncryptionTheme {
        Text("Android")
    }
}