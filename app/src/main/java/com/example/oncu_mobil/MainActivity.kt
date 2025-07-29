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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // SharedPreferences
        val sharedPref = getSharedPreferences("prefs", Context.MODE_PRIVATE)

        setContent {
            ONCU_MobilTheme {
                AppNavigation(sharedPref = sharedPref)
            }
        }
    }
}

@Composable
fun AppNavigation(sharedPref: android.content.SharedPreferences) {
    var currentScreen by remember { mutableStateOf("login") }
    var isLoggedIn by remember {
        mutableStateOf(sharedPref.getBoolean("loggedIn", false))
    }

    // Login durumuna göre başlangıç ekranını belirle
    LaunchedEffect(isLoggedIn) {
        currentScreen = if (isLoggedIn) "formList" else "login"
    }

    when (currentScreen) {
        "login" -> {
            LoginScreen(
                sharedPref = sharedPref,
                onLoginSuccess = {
                    isLoggedIn = true
                    currentScreen = "formList"
                }
            )
        }

        "formList" -> {
            FormListScreen(
                onFormSelected = { formName ->
                    when (formName) {
                        "Sıcak Dolum Hattı Paketleme Proses Kontrol Formu" -> {
                            currentScreen = "sdhkf"
                        }
                        "Anket Formu" -> {
                            // Diğer formlar için navigation ekleyebilirsiniz
                            // currentScreen = "anket"
                        }
                        "Geri Bildirim" -> {
                            // Diğer formlar için navigation ekleyebilirsiniz
                            // currentScreen = "geriBildirim"
                        }
                    }
                },
                onLogout = {
                    sharedPref.edit().putBoolean("loggedIn", false).apply()
                    isLoggedIn = false
                    currentScreen = "login"
                }
            )
        }

        "sdhkf" -> {
            SDHKF(
                onBack = {
                    currentScreen = "formList"
                }
            )
        }

        // Diğer formlar için case'ler buraya eklenebilir
        // "anket" -> { AnketFormu(onBack = { currentScreen = "formList" }) }
        // "geriBildirim" -> { GeriBildirimFormu(onBack = { currentScreen = "formList" }) }
    }

    // Geri tuşu kontrolü
    BackHandler(enabled = currentScreen != "login") {
        when (currentScreen) {
            "formList" -> {
                // Form listesindeyken geri tuşuna basılırsa çıkış yap
                sharedPref.edit().putBoolean("loggedIn", false).apply()
                isLoggedIn = false
                currentScreen = "login"
            }
            "sdhkf" -> {
                // Form sayfasındayken geri tuşuna basılırsa form listesine dön
                currentScreen = "formList"
            }
            // Diğer sayfalar için de benzer kontroller eklenebilir
        }
    }
}

@Composable
fun LoginScreen(sharedPref: android.content.SharedPreferences, onLoginSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Giriş Yap", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // LOGO BURADA
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .height(120.dp)
                .width(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                errorMessage = null // Hata mesajını temizle
            },
            label = { Text("Kullanıcı Adı") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = null // Hata mesajını temizle
            },
            label = { Text("Şifre") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it },
                enabled = !isLoading
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Beni Hatırla")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading = true
                // Basit doğrulama
                if (username.trim() == "admin" && password == "1234") {
                    // Başarılı giriş
                    sharedPref.edit().putBoolean("loggedIn", true).apply()
                    if (rememberMe) {
                        sharedPref.edit().putBoolean("rememberMe", true).apply()
                    } else {
                        sharedPref.edit().putBoolean("rememberMe", false).apply()
                    }
                    errorMessage = null
                    isLoading = false
                    onLoginSuccess()
                } else {
                    errorMessage = "Kullanıcı adı veya şifre yanlış"
                    isLoading = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && username.isNotBlank() && password.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Giriş")
            }
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
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
