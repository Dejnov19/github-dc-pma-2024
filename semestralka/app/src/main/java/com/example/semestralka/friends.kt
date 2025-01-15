package com.example.semestralka

// Importy potřebné pro Jetpack Compose a Firebase
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

// Komponenta pro obrazovku s přáteli
@Composable
fun FriendsForm(navController: NavController) {
    var friendsFlow by remember { mutableStateOf(getUserFriendsFromFirestore()) } // Data přátel z Firestore
    val friends by friendsFlow.collectAsState(initial = emptyList()) // Poslouchání změn dat
    var isDialogOpen by remember { mutableStateOf(false) } // Stav dialogu (otevřený/zavřený)
    val context = LocalContext.current // Kontext aplikace

    // Rozvržení obrazovky
    Column(
        modifier = Modifier
            .fillMaxSize() // Sloupec zabírá celou obrazovku
            .padding(50.dp), // Vnější odsazení
        verticalArrangement = Arrangement.SpaceBetween // Rozložení prvků ve sloupci
    ) {
        // Seznam přátel
        LazyColumn(
            modifier = Modifier.weight(1f), // Zabírá dostupný prostor
            verticalArrangement = Arrangement.spacedBy(8.dp) // Mezera mezi položkami
        ) {
            items(friends) { friend ->
                FriendItem(
                    friendName = friend,
                    onRemoveFriend = {
                        // Odebrání přítele z databáze a obnovení dat
                        removeFriendFromDatabase(friend, context) { friendsFlow = getUserFriendsFromFirestore() }
                    }
                )
            }
        }

        // Řádek s tlačítky
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween, // Zarovnání tlačítek na kraje
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Text(stringResource(R.string.Back)) // Tlačítko zpět
            }
            IconButton(onClick = { isDialogOpen = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.Add)) // Tlačítko pro přidání
            }
        }
    }

    // Dialog pro přidání přítele
    if (isDialogOpen) {
        FriendsDialog(
            onDismissRequest = { isDialogOpen = false }, // Zavření dialogu
            onConfirmation = { friendName ->
                addFriendToDatabase(friendName, context) { friendsFlow = getUserFriendsFromFirestore() } // Přidání přítele
                isDialogOpen = false
            }
        )
    }
}

// Položka seznamu přátel
@Composable
fun FriendItem(friendName: String, onRemoveFriend: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), // Vnější odsazení
        horizontalArrangement = Arrangement.SpaceBetween, // Zarovnání na kraje
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = friendName, modifier = Modifier.weight(1f)) // Jméno přítele
        IconButton(onClick = onRemoveFriend) {
            Text("❌") // Ikona pro odstranění
        }
    }
}

// Funkce pro odstranění přítele z databáze
fun removeFriendFromDatabase(friendName: String, context: Context, onSuccess: () -> Unit) {
    val db = Firebase.firestore
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid
    if (userId != null) {
        db.collection("users")
            .whereEqualTo("nickname", friendName) // Hledání přítele podle přezdívky
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    db.collection("users").document(userId)
                        .update("friends", FieldValue.arrayRemove(document.id)) // Odebrání přítele
                        .addOnSuccessListener {
                            Toast.makeText(context, R.string.friend_deleted, Toast.LENGTH_SHORT).show() // Notifikace úspěchu
                            onSuccess()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, R.string.Friend_doesnt_exist, Toast.LENGTH_SHORT).show() // Notifikace chyby
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, R.string.Friend_doesnt_exist, Toast.LENGTH_SHORT).show() // Notifikace chyby
            }
    }
}

// Získání seznamu přátel z Firestore
fun getUserFriendsFromFirestore(): Flow<List<String>> = flow {
    val db = Firebase.firestore
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid
    if (userId != null) {
        val userDoc = db.collection("users").document(userId).get().await()
        val friendIds = userDoc.get("friends") as? List<String> ?: emptyList()
        val friendsList = mutableListOf<String>()

        for (friendId in friendIds) {
            val friendDoc = db.collection("users").document(friendId).get().await()
            val nickname = friendDoc.getString("nickname")
            nickname?.let {
                friendsList.add(it)
            }
        }

        emit(friendsList) // Vrací seznam přátel
    } else {
        emit(emptyList()) // Pokud není uživatel přihlášen
    }
}

// Přidání přítele do databáze
fun addFriendToDatabase(friendName: String, context: Context, onSuccess: () -> Unit) {
    val db = Firebase.firestore
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid
    if (userId != null) {
        db.collection("users")
            .whereEqualTo("nickname", friendName) // Hledání přítele podle přezdívky
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    Toast.makeText(context, R.string.Friend_doesnt_exist, Toast.LENGTH_SHORT).show() // Notifikace, pokud přítel neexistuje
                } else {
                    for (document in querySnapshot.documents) {
                        db.collection("users").document(userId)
                            .update("friends", FieldValue.arrayUnion(document.id)) // Přidání přítele
                            .addOnSuccessListener {
                                Toast.makeText(context, R.string.friend_added, Toast.LENGTH_SHORT).show()
                                onSuccess()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, R.string.Friend_doesnt_exist, Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, R.string.Friend_doesnt_exist, Toast.LENGTH_SHORT).show()
            }
    }
}

// Dialog pro přidání přítele
@Composable
fun FriendsDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit
) {
    var friendNameText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismissRequest() }, // Zavření dialogu
        title = { Text(text = stringResource(R.string.Friend_Name)) },
        text = {
            OutlinedTextField(
                value = friendNameText, // Textové pole pro jméno přítele
                onValueChange = { friendNameText = it },
                label = { Text(stringResource(R.string.Friend_Name)) },
                placeholder = { Text(stringResource(R.string.Friend_Name)) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirmation(friendNameText) // Potvrzení přidání přítele
            }) {
                Text(stringResource(R.string.Confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(stringResource(R.string.exit)) // Tlačítko zavření dialogu
            }
        }
    )
}
