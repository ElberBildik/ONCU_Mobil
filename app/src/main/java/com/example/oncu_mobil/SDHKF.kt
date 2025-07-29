package com.example.oncu_mobil

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SDHKF(onBack: () -> Unit) {
    val context = LocalContext.current
    var formData by remember { mutableStateOf(loadFormData(context)) }
    val calendar = Calendar.getInstance()

    // Eğer tarih boşsa bugünün tarihini set et
    LaunchedEffect(Unit) {
        if (formData.paketlemeTarihi.isEmpty()) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            formData = formData.copy(paketlemeTarihi = dateFormat.format(calendar.time))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Başlık
        Text(
            text = "Sıcak Dolum Hattı Kalite Kontrol Formu",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Paketleme Bölümü
        PaketlemeBolumu(
            formData = formData,
            onFormDataChanged = { formData = it },
            context = context,
            calendar = calendar
        )

        Spacer(modifier = Modifier.height(16.dp))

        // İnjectleme Bölümü
        InjectlemeBolumu(
            formData = formData,
            onFormDataChanged = { formData = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Hologram Bölümü
        HologramBolumu(
            formData = formData,
            onFormDataChanged = { formData = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Etiketleme Bölümü
        EtiketlemeBolumu(
            formData = formData,
            onFormDataChanged = { formData = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Kolileme Bölümü
        KolilemeBolumu(
            formData = formData,
            onFormDataChanged = { formData = it }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Kaydet ve Geri Butonları
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Geri")
            }

            Button(
                onClick = {
                    if (validateForm(formData)) {
                        saveFormData(context, formData)
                        Toast.makeText(context, "Form başarıyla kaydedildi!", Toast.LENGTH_SHORT).show()
                        onBack()
                    } else {
                        Toast.makeText(context, "Lütfen tüm gerekli alanları doldurunuz!", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Kaydet")
            }
        }

        // Temizle butonu ekle
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = {
                clearFormData(context)
                formData = SDHKFFormData()
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                formData = formData.copy(paketlemeTarihi = dateFormat.format(calendar.time))
                Toast.makeText(context, "Form temizlendi!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Formu Temizle")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaketlemeBolumu(
    formData: SDHKFFormData,
    onFormDataChanged: (SDHKFFormData) -> Unit,
    context: android.content.Context,
    calendar: Calendar
) {
    val paketlemeHatlari = listOf(
        "Domates Hattı",
        "Biber 1 Hattı",
        "Biber 2 Hattı",
        "Biber 3 Hattı"
    )

    val urunAmbalajlari = listOf(
        "920cc-Pet",
        "1500cc-Pet",
        "3000cc-Pet",
        "3785cc-Pet"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Paketleme",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Paketleme Hattı
            Text(
                text = "Paketleme Hattı",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            var expandedHat by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expandedHat,
                onExpandedChange = { expandedHat = !expandedHat }
            ) {
                TextField(
                    value = formData.paketlemeHatti,
                    onValueChange = { },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedHat) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedHat,
                    onDismissRequest = { expandedHat = false }
                ) {
                    paketlemeHatlari.forEach { hat ->
                        DropdownMenuItem(
                            text = { Text(hat) },
                            onClick = {
                                onFormDataChanged(formData.copy(paketlemeHatti = hat))
                                expandedHat = false
                            }
                        )
                    }
                }
            }

            // Paketleme Tarihi
            Text(
                text = "Paketleme Tarihi",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            TextField(
                value = formData.paketlemeTarihi,
                onValueChange = { },
                readOnly = true,
                placeholder = { Text("DD/MM/YYYY") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                calendar.set(Calendar.YEAR, year)
                                calendar.set(Calendar.MONTH, month)
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                onFormDataChanged(formData.copy(paketlemeTarihi = dateFormat.format(calendar.time)))
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }
                    .padding(bottom = 16.dp)
            )

            // Parti No
            Text(
                text = "Parti No",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            TextField(
                value = formData.partiNo,
                onValueChange = { newValue ->
                    // Sadece rakam ve tire kabul et
                    val filteredValue = newValue.filter { it.isDigit() || it == '-' }

                    // Eğer tire yoksa ve 3 karakter varsa otomatik tire ekle
                    val formattedValue = if (filteredValue.length == 3 && !filteredValue.contains("-")) {
                        "$filteredValue-"
                    } else if (filteredValue.length <= 5) {
                        filteredValue
                    } else {
                        formData.partiNo // Değişiklik yapma, limit aşıldı
                    }

                    onFormDataChanged(formData.copy(partiNo = formattedValue))
                },
                placeholder = { Text("000-0") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Ürün Ambalajı
            Text(
                text = "Ürün Ambalajı",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            var expandedAmbalaj by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expandedAmbalaj,
                onExpandedChange = { expandedAmbalaj = !expandedAmbalaj }
            ) {
                TextField(
                    value = formData.urunAmbalaji,
                    onValueChange = { },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAmbalaj) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedAmbalaj,
                    onDismissRequest = { expandedAmbalaj = false }
                ) {
                    urunAmbalajlari.forEach { ambalaj ->
                        DropdownMenuItem(
                            text = { Text(ambalaj) },
                            onClick = {
                                onFormDataChanged(formData.copy(urunAmbalaji = ambalaj))
                                expandedAmbalaj = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InjectlemeBolumu(
    formData: SDHKFFormData,
    onFormDataChanged: (SDHKFFormData) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "İnjectleme",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Pazar Seçimi
            Text(
                text = "Pazar",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .selectable(
                            selected = formData.injectlemePazar == "İç Piyasa",
                            onClick = { onFormDataChanged(formData.copy(injectlemePazar = "İç Piyasa")) }
                        )
                        .padding(end = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = formData.injectlemePazar == "İç Piyasa",
                        onClick = { onFormDataChanged(formData.copy(injectlemePazar = "İç Piyasa")) }
                    )
                    Text("İç Piyasa", modifier = Modifier.padding(start = 8.dp))
                }

                Row(
                    modifier = Modifier.selectable(
                        selected = formData.injectlemePazar == "İhraç",
                        onClick = { onFormDataChanged(formData.copy(injectlemePazar = "İhraç")) }
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = formData.injectlemePazar == "İhraç",
                        onClick = { onFormDataChanged(formData.copy(injectlemePazar = "İhraç")) }
                    )
                    Text("İhraç", modifier = Modifier.padding(start = 8.dp))
                }
            }

            // TETT
            Text(
                text = "TETT/BBE/MHD/TETT",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            TextField(
                value = formData.tett,
                onValueChange = { onFormDataChanged(formData.copy(tett = it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // PNO
            Text(
                text = "PNO/SNO",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            TextField(
                value = formData.pno,
                onValueChange = { onFormDataChanged(formData.copy(pno = it)) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun HologramBolumu(
    formData: SDHKFFormData,
    onFormDataChanged: (SDHKFFormData) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Hologram",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row {
                Row(
                    modifier = Modifier
                        .selectable(
                            selected = formData.hologram == "Var",
                            onClick = { onFormDataChanged(formData.copy(hologram = "Var")) }
                        )
                        .padding(end = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = formData.hologram == "Var",
                        onClick = { onFormDataChanged(formData.copy(hologram = "Var")) }
                    )
                    Text("Var", modifier = Modifier.padding(start = 8.dp))
                }

                Row(
                    modifier = Modifier.selectable(
                        selected = formData.hologram == "Yok",
                        onClick = { onFormDataChanged(formData.copy(hologram = "Yok")) }
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = formData.hologram == "Yok",
                        onClick = { onFormDataChanged(formData.copy(hologram = "Yok")) }
                    )
                    Text("Yok", modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EtiketlemeBolumu(
    formData: SDHKFFormData,
    onFormDataChanged: (SDHKFFormData) -> Unit
) {
    val urunAdlari = listOf("Domates", "Acı Biber", "Tatlı Biber", "Karışık")
    val gramajBilgileri = listOf("900", "910", "1600", "1650", "3200", "4300")

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Etiketleme",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Pazar Seçimi
            Text(
                text = "Pazar",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .selectable(
                            selected = formData.etiketlemePazar == "İç Piyasa",
                            onClick = { onFormDataChanged(formData.copy(etiketlemePazar = "İç Piyasa")) }
                        )
                        .padding(end = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = formData.etiketlemePazar == "İç Piyasa",
                        onClick = { onFormDataChanged(formData.copy(etiketlemePazar = "İç Piyasa")) }
                    )
                    Text("İç Piyasa", modifier = Modifier.padding(start = 8.dp))
                }

                Row(
                    modifier = Modifier.selectable(
                        selected = formData.etiketlemePazar == "İhraç",
                        onClick = { onFormDataChanged(formData.copy(etiketlemePazar = "İhraç")) }
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = formData.etiketlemePazar == "İhraç",
                        onClick = { onFormDataChanged(formData.copy(etiketlemePazar = "İhraç")) }
                    )
                    Text("İhraç", modifier = Modifier.padding(start = 8.dp))
                }
            }

            // Ürün Adı
            Text(
                text = "Ürün Adı",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            var expandedUrun by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expandedUrun,
                onExpandedChange = { expandedUrun = !expandedUrun }
            ) {
                TextField(
                    value = formData.etiketlemeUrunAdi,
                    onValueChange = { },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUrun) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedUrun,
                    onDismissRequest = { expandedUrun = false }
                ) {
                    urunAdlari.forEach { urun ->
                        DropdownMenuItem(
                            text = { Text(urun) },
                            onClick = {
                                onFormDataChanged(formData.copy(etiketlemeUrunAdi = urun))
                                expandedUrun = false
                            }
                        )
                    }
                }
            }

            // Gramaj Bilgisi
            Text(
                text = "Gramaj Bilgisi",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            var expandedGramaj by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expandedGramaj,
                onExpandedChange = { expandedGramaj = !expandedGramaj }
            ) {
                TextField(
                    value = formData.etiketlemeGramaj,
                    onValueChange = { },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGramaj) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedGramaj,
                    onDismissRequest = { expandedGramaj = false }
                ) {
                    gramajBilgileri.forEach { gramaj ->
                        DropdownMenuItem(
                            text = { Text(gramaj) },
                            onClick = {
                                onFormDataChanged(formData.copy(etiketlemeGramaj = gramaj))
                                expandedGramaj = false
                            }
                        )
                    }
                }
            }

            // Etiket Lot No
            Text(
                text = "Etiket Lot No",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            TextField(
                value = formData.etiketLotNo,
                onValueChange = { onFormDataChanged(formData.copy(etiketLotNo = it)) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KolilemeBolumu(
    formData: SDHKFFormData,
    onFormDataChanged: (SDHKFFormData) -> Unit
) {
    val urunAdlari = listOf("Domates", "Acı Biber", "Tatlı Biber", "Karışık")
    val gramajBilgileri = listOf("900", "910", "1600", "1650", "3200", "4300")

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Kolileme",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Pazar Seçimi
            Text(
                text = "Pazar",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .selectable(
                            selected = formData.kolilemePazar == "İç Piyasa",
                            onClick = { onFormDataChanged(formData.copy(kolilemePazar = "İç Piyasa")) }
                        )
                        .padding(end = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = formData.kolilemePazar == "İç Piyasa",
                        onClick = { onFormDataChanged(formData.copy(kolilemePazar = "İç Piyasa")) }
                    )
                    Text("İç Piyasa", modifier = Modifier.padding(start = 8.dp))
                }

                Row(
                    modifier = Modifier.selectable(
                        selected = formData.kolilemePazar == "İhraç",
                        onClick = { onFormDataChanged(formData.copy(kolilemePazar = "İhraç")) }
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = formData.kolilemePazar == "İhraç",
                        onClick = { onFormDataChanged(formData.copy(kolilemePazar = "İhraç")) }
                    )
                    Text("İhraç", modifier = Modifier.padding(start = 8.dp))
                }
            }

            // Ürün Adı
            Text(
                text = "Ürün Adı",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            var expandedKoliUrun by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expandedKoliUrun,
                onExpandedChange = { expandedKoliUrun = !expandedKoliUrun }
            ) {
                TextField(
                    value = formData.kolilemeUrunAdi,
                    onValueChange = { },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedKoliUrun) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedKoliUrun,
                    onDismissRequest = { expandedKoliUrun = false }
                ) {
                    urunAdlari.forEach { urun ->
                        DropdownMenuItem(
                            text = { Text(urun) },
                            onClick = {
                                onFormDataChanged(formData.copy(kolilemeUrunAdi = urun))
                                expandedKoliUrun = false
                            }
                        )
                    }
                }
            }

            // Gramaj Bilgisi
            Text(
                text = "Gramaj Bilgisi",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            var expandedKoliGramaj by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expandedKoliGramaj,
                onExpandedChange = { expandedKoliGramaj = !expandedKoliGramaj }
            ) {
                TextField(
                    value = formData.kolilemeGramaj,
                    onValueChange = { },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedKoliGramaj) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedKoliGramaj,
                    onDismissRequest = { expandedKoliGramaj = false }
                ) {
                    gramajBilgileri.forEach { gramaj ->
                        DropdownMenuItem(
                            text = { Text(gramaj) },
                            onClick = {
                                onFormDataChanged(formData.copy(kolilemeGramaj = gramaj))
                                expandedKoliGramaj = false
                            }
                        )
                    }
                }
            }

            // Koli Lot No
            Text(
                text = "Koli Lot No",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            TextField(
                value = formData.koliLotNo,
                onValueChange = { onFormDataChanged(formData.copy(koliLotNo = it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Koliye Uygunluk
            Text(
                text = "Koliye Uygunluk",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row {
                Row(
                    modifier = Modifier
                        .selectable(
                            selected = formData.koliyeUygunluk == "Uygun",
                            onClick = { onFormDataChanged(formData.copy(koliyeUygunluk = "Uygun")) }
                        )
                        .padding(end = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = formData.koliyeUygunluk == "Uygun",
                        onClick = { onFormDataChanged(formData.copy(koliyeUygunluk = "Uygun")) }
                    )
                    Text("Uygun", modifier = Modifier.padding(start = 8.dp))
                }

                Row(
                    modifier = Modifier.selectable(
                        selected = formData.koliyeUygunluk == "Değil",
                        onClick = { onFormDataChanged(formData.copy(koliyeUygunluk = "Değil")) }
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = formData.koliyeUygunluk == "Değil",
                        onClick = { onFormDataChanged(formData.copy(koliyeUygunluk = "Değil")) }
                    )
                    Text("Değil", modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}

// Veri sınıfı
data class SDHKFFormData(
    val paketlemeHatti: String = "",
    val paketlemeTarihi: String = "",
    val partiNo: String = "",
    val urunAmbalaji: String = "",
    val injectlemePazar: String = "İç Piyasa",
    val tett: String = "",
    val pno: String = "",
    val hologram: String = "Var",
    val etiketlemePazar: String = "İç Piyasa",
    val etiketlemeUrunAdi: String = "",
    val etiketlemeGramaj: String = "",
    val etiketLotNo: String = "",
    val kolilemePazar: String = "İç Piyasa",
    val kolilemeUrunAdi: String = "",
    val kolilemeGramaj: String = "",
    val koliLotNo: String = "",
    val koliyeUygunluk: String = ""
)

// Yardımcı fonksiyonlar
fun validateForm(formData: SDHKFFormData): Boolean {
    return formData.paketlemeHatti.isNotEmpty() &&
            formData.paketlemeTarihi.isNotEmpty() &&
            formData.partiNo.isNotEmpty() &&
            formData.urunAmbalaji.isNotEmpty() &&
            formData.tett.isNotEmpty() &&
            formData.pno.isNotEmpty() &&
            formData.etiketlemeUrunAdi.isNotEmpty() &&
            formData.etiketlemeGramaj.isNotEmpty() &&
            formData.etiketLotNo.isNotEmpty() &&
            formData.kolilemeUrunAdi.isNotEmpty() &&
            formData.kolilemeGramaj.isNotEmpty() &&
            formData.koliLotNo.isNotEmpty() &&
            formData.koliyeUygunluk.isNotEmpty()
}

fun saveFormData(context: android.content.Context, formData: SDHKFFormData) {
    val sharedPref = context.getSharedPreferences("SDHKFFormData", android.content.Context.MODE_PRIVATE)
    val editor = sharedPref.edit()

    // Form verilerini kaydet
    editor.putString("paketlemeHatti", formData.paketlemeHatti)
    editor.putString("paketlemeTarihi", formData.paketlemeTarihi)
    editor.putString("partiNo", formData.partiNo)
    editor.putString("urunAmbalaji", formData.urunAmbalaji)
    editor.putString("injectlemePazar", formData.injectlemePazar)
    editor.putString("tett", formData.tett)
    editor.putString("pno", formData.pno)
    editor.putString("hologram", formData.hologram)
    editor.putString("etiketlemePazar", formData.etiketlemePazar)
    editor.putString("etiketlemeUrunAdi", formData.etiketlemeUrunAdi)
    editor.putString("etiketlemeGramaj", formData.etiketlemeGramaj)
    editor.putString("etiketLotNo", formData.etiketLotNo)
    editor.putString("kolilemePazar", formData.kolilemePazar)
    editor.putString("kolilemeUrunAdi", formData.kolilemeUrunAdi)
    editor.putString("kolilemeGramaj", formData.kolilemeGramaj)
    editor.putString("koliLotNo", formData.koliLotNo)
    editor.putString("koliyeUygunluk", formData.koliyeUygunluk)

    // Kaydetme tarihini de ekle
    val currentTime = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
    editor.putString("kaydetmeTarihi", currentTime)

    editor.apply()
}

// Form verilerini yükle
fun loadFormData(context: android.content.Context): SDHKFFormData {
    val sharedPref = context.getSharedPreferences("SDHKFFormData", android.content.Context.MODE_PRIVATE)

    return SDHKFFormData(
        paketlemeHatti = sharedPref.getString("paketlemeHatti", "") ?: "",
        paketlemeTarihi = sharedPref.getString("paketlemeTarihi", "") ?: "",
        partiNo = sharedPref.getString("partiNo", "") ?: "",
        urunAmbalaji = sharedPref.getString("urunAmbalaji", "") ?: "",
        injectlemePazar = sharedPref.getString("injectlemePazar", "İç Piyasa") ?: "İç Piyasa",
        tett = sharedPref.getString("tett", "") ?: "",
        pno = sharedPref.getString("pno", "") ?: "",
        hologram = sharedPref.getString("hologram", "Var") ?: "Var",
        etiketlemePazar = sharedPref.getString("etiketlemePazar", "İç Piyasa") ?: "İç Piyasa",
        etiketlemeUrunAdi = sharedPref.getString("etiketlemeUrunAdi", "") ?: "",
        etiketlemeGramaj = sharedPref.getString("etiketlemeGramaj", "") ?: "",
        etiketLotNo = sharedPref.getString("etiketLotNo", "") ?: "",
        kolilemePazar = sharedPref.getString("kolilemePazar", "İç Piyasa") ?: "İç Piyasa",
        kolilemeUrunAdi = sharedPref.getString("kolilemeUrunAdi", "") ?: "",
        kolilemeGramaj = sharedPref.getString("kolilemeGramaj", "") ?: "",
        koliLotNo = sharedPref.getString("koliLotNo", "") ?: "",
        koliyeUygunluk = sharedPref.getString("koliyeUygunluk", "") ?: ""
    )
}

// Form verilerini temizle
fun clearFormData(context: android.content.Context) {
    val sharedPref = context.getSharedPreferences("SDHKFFormData", android.content.Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.clear()
    editor.apply()
}