package com.example.saysalloto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModel
import com.example.saysalloto.ui.theme.SayısalLotoTheme

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SayısalLotoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "Sayısal Loto 6/49",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    color = Color.White
                                )
                            },
                            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(red = 117, green = 0, blue = 46))
                        )
                        KolonSayisi(viewModel)
                        UgurluSayiGoster(viewModel)
                    }
                }
            }
        }

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun KolonSayisi(viewModel: MainViewModel) {
        var kolonSayisi by remember { mutableStateOf(0) }
        val outlinedTextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color(red = 117, green = 0, blue = 46),
            unfocusedBorderColor = Color(red = 206, green = 37, blue = 90),
            textColor = Color.Black
        )
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ){
            OutlinedTextField(
                value = kolonSayisi.takeIf { it > 0 }?.toString() ?: "",
                onValueChange = {
                    kolonSayisi = it.toIntOrNull() ?: 0
                },
                label = { Text(text = "Kolon sayısı girin: ") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                colors = outlinedTextFieldColors,
                modifier = Modifier.padding(16.dp)
            )

            repeat(kolonSayisi) { kolonIndex ->
                KolonSiralama(viewModel.ugurluSayilar)
                if (kolonIndex < kolonSayisi - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    @Composable
    fun KolonSiralama(ugurluSayilar: List<String>) {
        val deger = 6 - ugurluSayilar.count()
        val randomSayilar = (1..49).toList().shuffled().filterNot { it.toString() in ugurluSayilar }.take(deger)

        val tumSayilar = (randomSayilar.map { it.toString() } + ugurluSayilar)
            .mapNotNull { it.toIntOrNull() }
            .sorted()
            .toSet()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center)
        ) {
            tumSayilar.forEach { sayi ->
                YuvarlakliSayi(sayi)
            }
        }
    }

    @Composable
    fun YuvarlakliSayi(sayi: Int) {
        Box(
            modifier = Modifier
                .padding(end = 6.dp)
                .size(50.dp)
                .background(Color(red = 206, green = 37, blue = 90), shape = CircleShape)
        ) {
            Text(
                text = "$sayi",
                color = Color.White,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                textAlign = TextAlign.Center
            )
        }
    }


    @Composable
    fun UgurluSayiGoster(viewModel: MainViewModel) {
        val buttonColor = Color(red = 117, green = 0, blue = 46)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Button(onClick = {
                viewModel.showDialog = true
            },
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor)) {
                Text("Uğurlu Sayıları Gir")
            }

            if (viewModel.showDialog) {
                NumberInputDialog(
                    onDismiss = { viewModel.showDialog = false },
                    onDone = { girilenSayi1, girilenSayi2 ->
                        viewModel.ugurluSayilar = listOf(girilenSayi1, girilenSayi2)
                        viewModel.showDialog = false
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (viewModel.ugurluSayilar.isNotEmpty()) {
                Text("Uğurlu Sayılar: ${viewModel.ugurluSayilar.joinToString(" ")}")
            }
        }
    }
    class MainViewModel : ViewModel() {
        var ugurluSayilar by mutableStateOf(emptyList<String>())
        var showDialog by mutableStateOf(false)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun NumberInputDialog(onDismiss: () -> Unit, onDone: (String, String) -> Unit) {
        var ugurluSayi1 by remember { mutableStateOf("") }
        var ugurluSayi2 by remember { mutableStateOf("") }
        val buttonColor = Color(red = 117, green = 0, blue = 46)
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Uğurlu Sayılarınızı Girin") },
            text = {
                Column {
                    OutlinedTextField(
                        value = ugurluSayi1,
                        onValueChange = { ugurluSayi1 = it },
                        label = { Text("Uğurlu Sayı 1") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = ugurluSayi2,
                        onValueChange = { ugurluSayi2 = it },
                        label = { Text("Uğurlu Sayı 2") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton =  {
                Button(
                    onClick = {
                        onDone(ugurluSayi1, ugurluSayi2)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                ) {
                    Text("Tamam")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                ) {
                    Text("İptal")
                }
            },
        )
    }

}

