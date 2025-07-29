package com.example.oncu_mobil

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.oncu_mobil.ui.theme.ONCU_MobilTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // SharedPreferences (tercih ettiğimiz adı "prefs")
        val sharedPref = getSharedPreferences("prefs", Context.MODE_PRIVATE)

        // Oturum var mı kontrol et
        val loggedIn = sharedPref.getBoolean("loggedIn", false)

        setContent {
            ONCU_MobilTheme {
                if (loggedIn) {
                    HomeScreen(
                        onLogout = {
                            sharedPref.edit().putBoolean("loggedIn", false).apply()
                            // yeniden compose tetiklenmeli, bunun için State kullanılabilir
                            // burada simplest haliyle MainActivity'yi yeniden başlatabilirsin
                            recreate()
                        }
                    )
                } else {
                    LoginScreen(
                        sharedPref = sharedPref,
                        onLoginSuccess = {
                            recreate() // Giriş başarılıysa aktiviteyi yeniden başlat
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LoginScreen(sharedPref: android.content.SharedPreferences, onLoginSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Giriş Yap", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Kullanıcı Adı") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Şifre") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Beni Hatırla")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Burada basit bir doğrulama yapalım (gerçek projede API ile kontrol edilir)
                if (username == "admin" && password == "1234") {
                    if (rememberMe) {
                        sharedPref.edit().putBoolean("loggedIn", true).apply()
                    } else {
                        sharedPref.edit().putBoolean("loggedIn", false).apply()
                    }
                    errorMessage = null
                    onLoginSuccess()
                } else {
                    errorMessage = "Kullanıcı adı veya şifre yanlış"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Giriş")
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun HomeScreen(onLogout: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Hoşgeldiniz!", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onLogout) {
                Text("Çıkış Yap")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    ONCU_MobilTheme {
        LoginScreen(
            sharedPref = androidx.compose.ui.platform.LocalContext.current.getSharedPreferences("prefs", Context.MODE_PRIVATE),
            onLoginSuccess = {}
        )
    }
}
