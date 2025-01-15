package com.example.semestralka

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun LogForm(navController: NavController) {
    // Proměnné pro uložení emailu a hesla uživatele
    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    val buttonWidth = 150.dp
    val context = LocalContext.current // Získání aktuálního kontextu aplikace

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        // Zobrazení uvítacího textu
        Text(
            text = stringResource(R.string.welcome),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 100.dp, bottom = 50.dp)
        )

        // Textové pole pro zadání emailu
        OutlinedTextField(
            value = emailText,
            onValueChange = { emailText = it },
            label = { Text("Email") },
            placeholder = { Text(stringResource(R.string.Enter_your_email)) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }, // Ikona před polem
            trailingIcon = { Icon(Icons.Default.Clear, contentDescription = null) }, // Ikona za polem
            modifier = Modifier.fillMaxWidth().padding(20.dp)
        )

        // Textové pole pro zadání hesla
        OutlinedTextField(
            value = passwordText,
            onValueChange = { passwordText = it },
            label = { Text(stringResource(R.string.password)) },
            placeholder = { Text(stringResource(R.string.Enter_your_password)) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = { Icon(Icons.Default.Clear, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().padding(20.dp)
        )

        // Tlačítko pro přihlášení
        FilledTonalButton(
            onClick = {
                // Přihlášení uživatele pomocí emailu a hesla
                signInWithEmailAndPassword(emailText, passwordText, navController, context)
            },
            modifier = Modifier.width(buttonWidth)
        ) {
            Text(stringResource(R.string.login))
        }
    }
}

// Funkce pro přihlášení uživatele pomocí Firebase
private fun signInWithEmailAndPassword(email: String, password: String, navController: NavController, context: Context) {
    val auth = Firebase.auth // Inicializace Firebase autentizace

    // Ověření, zda nejsou vstupní pole prázdná
    if (email.isBlank() || password.isBlank()) {
        Toast.makeText(context, context.getString(R.string.Cannot_be_empty), Toast.LENGTH_SHORT).show()
        return
    }

    // Přihlášení pomocí Firebase
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Pokud je přihlášení úspěšné, naviguje na obrazovku "setup"
                navController.navigate("setup")
            } else {
                // Pokud přihlášení selže, zobrazí chybovou hlášku
                Toast.makeText(
                    context,
                    context.getString(R.string.wrong),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
}
