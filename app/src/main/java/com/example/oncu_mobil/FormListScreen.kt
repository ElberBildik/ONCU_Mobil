package com.example.oncu_mobil

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FormListScreen(onFormSelected: (String) -> Unit, onLogout: () -> Unit) {
    val forms = listOf("Sıcak Dolum Hattı Paketleme Proses Kontrol Formu", "Anket Formu", "Geri Bildirim")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Formlar", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        forms.forEach { form ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        onFormSelected(form)
                    }
            ) {
                Text(
                    text = form,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onLogout) {
            Text("Çıkış Yap")
        }
    }
}

