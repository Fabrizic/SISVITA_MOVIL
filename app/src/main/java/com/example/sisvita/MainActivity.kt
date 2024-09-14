// MainActivity.kt
package com.example.sisvita

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.sisvita.ui.theme.SISVITATheme
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SISVITATheme {
                var showErrorDialog by remember { mutableStateOf(false) }

                if (showErrorDialog) {
                    ErrorDialog { showErrorDialog = false }
                }

                LoginScreen(
                    onLogin = { email, password ->
                        login(email, password) { success ->
                            if (success) {
                                startActivity(Intent(this, HomeActivity::class.java))
                                finish()
                            } else {
                                showErrorDialog = true
                            }
                        }
                    },
                    onSignUp = {
                        startActivity(Intent(this, RegisterActivity::class.java))
                    }
                )
            }
        }
    }

    private fun login(email: String, password: String, callback: (Boolean) -> Unit) {
        val client = OkHttpClient()
        val json = JSONObject().apply {
            put("correo", email)
            put("contrasena", password)
            put("tipousuarioid", 1)
        }

        val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("https://sysvita-dswg13-production.up.railway.app/login")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        callback(false)
                        return
                    }

                    val responseBody = it.body?.string()
                    if (responseBody != null) {
                        val json1 = JSONObject(responseBody)
                        val message = json1.optString("message")
                        val status = json1.optInt("status")

                        if (status == 200 && message == "Login exitoso") {
                            callback(true)
                        } else {
                            callback(false)
                        }
                    } else {
                        callback(false)
                    }
                }
            }
        })
    }
}

@Composable
fun LoginScreen(onLogin: (String, String) -> Unit, onSignUp: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onLogin(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Don't have an account? Sign up",
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSignUp() }
                .padding(8.dp)
        )
    }
}

@Composable
fun ErrorDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Error") },
        text = { Text("Correo o contraseña incorrectas") },
        confirmButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("OK")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    SISVITATheme {
        LoginScreen(onLogin = { _, _ -> }, onSignUp = {})
    }
}