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
import androidx.compose.material.icons.filled.Person
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun RegisterForm(navController: NavController) {
    // Proměnné pro email, přezdívku a heslo uživatele
    var emailText by remember { mutableStateOf("") }
    var nicknameText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    val buttonWidth = 150.dp
    val context = LocalContext.current // Získání aktuálního kontextu

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        // Text pro vytvoření nového uživatele
        Text(
            text = stringResource(R.string.New_user),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 100.dp, bottom = 50.dp)
        )

        // Pole pro zadání emailu
        OutlinedTextField(
            value = emailText,
            onValueChange = { emailText = it },
            label = { Text("Email") },
            placeholder = { Text(stringResource(R.string.Enter_your_email)) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().padding(20.dp)
        )

        // Pole pro zadání přezdívky
        OutlinedTextField(
            value = nicknameText,
            onValueChange = { nicknameText = it },
            label = { Text(stringResource(R.string.nickname)) },
            placeholder = { Text(stringResource(R.string.Enter_your_nickname)) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            trailingIcon = { Icon(Icons.Default.Clear, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().padding(20.dp)
        )

        // Pole pro zadání hesla
        OutlinedTextField(
            value = passwordText,
            onValueChange = { passwordText = it },
            label = { Text(stringResource(R.string.password)) },
            placeholder = { Text(stringResource(R.string.Enter_your_password)) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = { Icon(Icons.Default.Clear, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().padding(20.dp)
        )

        // Tlačítko pro registraci uživatele
        FilledTonalButton(
            onClick = {
                registerUser(emailText, nicknameText, passwordText, navController, context)
            },
            modifier = Modifier.width(buttonWidth)
        ) {
            Text(stringResource(R.string.register))
        }
    }
}

// Funkce pro registraci uživatele
private fun registerUser(email: String, nickname: String, password: String, navController: NavController, context: Context) {
    if (email.isBlank() || nickname.isBlank() || password.isBlank()) {
        // Zobrazení chyby, pokud jsou pole prázdná
        Toast.makeText(context, context.getString(R.string.Cannot_be_empty), Toast.LENGTH_SHORT).show()
        return
    }

    val auth = FirebaseAuth.getInstance() // Inicializace Firebase autentizace
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Pokud je registrace úspěšná, nastaví se uživatelský profil
                val user = auth.currentUser
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(nickname) // Nastavení přezdívky
                    .build()
                user?.updateProfile(profileUpdates)
                    ?.addOnCompleteListener { profileTask ->
                        if (profileTask.isSuccessful) {
                            Toast.makeText(context, context.getString(R.string.succ), Toast.LENGTH_SHORT).show()
                            addToDB(email, nickname) // Uložení uživatele do databáze
                            navController.navigate("setup") // Navigace na obrazovku "setup"
                        } else {
                            Toast.makeText(context, context.getString(R.string.unsucc), Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                // Pokud registrace selže
                Toast.makeText(context, context.getString(R.string.unsucc), Toast.LENGTH_SHORT).show()
            }
        }
}

// Funkce pro přidání uživatele do Firestore databáze
private fun addToDB(email: String, nickname: String) {
    val db = Firebase.firestore // Inicializace Firestore
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid
    if (userId != null) {
        val user = hashMapOf(
            "email" to email,
            "nickname" to nickname,
            "friends" to listOf<String>(), // Pole přátel
            "SingleFriends" to listOf<String>() // Pole "single přátel"
        )
        // Uložení dokumentu s ID uživatele
        db.collection("users")
            .document(userId)
            .set(user)
            .addOnSuccessListener {
                // Úspěšné uložení
            }
            .addOnFailureListener {
                // Chyba při ukládání
            }
    }
}
