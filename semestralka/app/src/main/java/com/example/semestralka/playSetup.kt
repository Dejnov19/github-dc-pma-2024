package com.example.semestralka

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

@Composable
fun PlaySetupForm(navController: NavController) {
    val friendNames = remember { mutableStateOf<List<String>>(emptyList()) }
    val showDialog = remember { mutableStateOf(false) }

    // Načtení seznamu hráčů z Firestore při spuštění obrazovky
    LaunchedEffect(Unit) {
        loadFriendListFromDatabase { loadedFriends ->
            friendNames.value = loadedFriends
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Seznam přátel
        friendNames.value.forEachIndexed { index, name ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = "${index + 1}: $name",
                    modifier = Modifier.weight(1f) // Zabírá volný prostor
                )
                Button(
                    onClick = {
                        friendNames.value = friendNames.value.toMutableList().apply { removeAt(index) }
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("❌") // Malý křížek pro odstranění
                }
            }
        }

        // Tlačítko pro přidání nového hráče (otevře dialog)
        Button(
            onClick = { showDialog.value = true },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(stringResource(R.string.Friend_add))
        }

        // Tlačítko pro start hry
        Button(
            onClick = {
                saveFriendListToDatabase(friendList = friendNames.value)
                navController.navigate("game")
            },
            enabled = friendNames.value.isNotEmpty(),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(stringResource(R.string.Start_game))
        }

        // Tlačítko zpět
        Button(
            onClick = {
                saveFriendListToDatabase(friendList = friendNames.value)
                navController.popBackStack() },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(stringResource(R.string.Back))
        }

        // Dialog pro zadání nového jména
        if (showDialog.value) {
            AddFriendDialog(
                onDismiss = { showDialog.value = false },
                onAdd = { name ->
                    friendNames.value += name
                    showDialog.value = false
                }
            )
        }
    }
}

@Composable
fun AddFriendDialog(onDismiss: () -> Unit, onAdd: (String) -> Unit) {
    val newFriendName = remember { mutableStateOf("") }

    // Dialog
    androidx.compose.material3.AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(stringResource(R.string.Friend_add)) },
        text = {
            Column {
                TextField(
                    value = newFriendName.value,
                    onValueChange = { newFriendName.value = it },
                    label = { Text(stringResource(R.string.Friend_Name)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newFriendName.value.isNotBlank()) {
                        onAdd(newFriendName.value)
                    }
                }
            ) {
                Text(stringResource(R.string.Add))
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text(stringResource(R.string.Cancel))
            }
        }
    )
}

// Funkce pro uložení seznamu přátel do Firestore
private fun saveFriendListToDatabase(friendList: List<String>) {
    val db = Firebase.firestore
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid
    if (userId != null) {
        val userRef = db.collection("users").document(userId)
        userRef.update("singleFriends", friendList)
            .addOnSuccessListener {
                // Data uložena úspěšně
            }
            .addOnFailureListener {
                // Selhání při ukládání dat
            }
    }
}
// Funkce pro načtení seznamu přátel z Firestore
private fun loadFriendListFromDatabase(onDataLoaded: (List<String>) -> Unit) {
    val db = Firebase.firestore
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid
    if (userId != null) {
        val userRef = db.collection("users").document(userId)
        userRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val friends = document.get("singleFriends") as? List<String> ?: emptyList()
                    onDataLoaded(friends)
                } else {
                    onDataLoaded(emptyList()) // Pokud neexistuje
                }
            }
            .addOnFailureListener {
                onDataLoaded(emptyList()) // Selhání načítání dat
            }
    } else {
        onDataLoaded(emptyList()) // Uživatel není přihlášen
    }
}