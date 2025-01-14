package com.example.semestralka

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
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
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

@Composable
fun FriendsForm(navController: NavController) {
    var friendsFlow by remember { mutableStateOf(getUserFriendsFromFirestore()) }
    val friends by friendsFlow.collectAsState(initial = emptyList())
    var isDialogOpen by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(friends) { friend ->
                FriendItem(
                    friendName = friend,
                    onRemoveFriend = { removeFriendFromDatabase(friend, context) { friendsFlow = getUserFriendsFromFirestore() } }
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Text(stringResource(R.string.Back))
            }
            IconButton(onClick = { isDialogOpen = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.Add))
            }
        }
    }

    if (isDialogOpen) {
        FriendsDialog(
            onDismissRequest = { isDialogOpen = false },
            onConfirmation = { friendName ->
                addFriendToDatabase(friendName, context) { friendsFlow = getUserFriendsFromFirestore() }
                isDialogOpen = false
            }
        )
    }
}

@Composable
fun FriendItem(friendName: String, onRemoveFriend: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = friendName, modifier = Modifier.weight(1f))
        IconButton(onClick = onRemoveFriend) {
            Text("❌") // Malý křížek pro odstranění
        }
    }
}

fun removeFriendFromDatabase(friendName: String, context: Context, onSuccess: () -> Unit) {
    val db = Firebase.firestore
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid
    if (userId != null) {
        db.collection("users")
            .whereEqualTo("nickname", friendName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    db.collection("users").document(userId)
                        .update("friends", FieldValue.arrayRemove(document.id))
                        .addOnSuccessListener {
                            Toast.makeText(context, R.string.friend_deleted, Toast.LENGTH_SHORT).show()
                            onSuccess()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, R.string.Friend_doesnt_exist, Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, R.string.Friend_doesnt_exist, Toast.LENGTH_SHORT).show()
            }
    }
}

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

        emit(friendsList)
    } else {
        emit(emptyList())
    }
}

fun addFriendToDatabase(friendName: String, context: Context, onSuccess: () -> Unit) {
    val db = Firebase.firestore
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid
    if (userId != null) {
        db.collection("users")
            .whereEqualTo("nickname", friendName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    // Pokud neexistuje žádný dokument, přezdívka nebyla nalezena
                    Toast.makeText(context, R.string.Friend_doesnt_exist, Toast.LENGTH_SHORT).show()
                } else {
                    // Pokud je přezdívka nalezena, přidej ji do databáze
                    for (document in querySnapshot.documents) {
                        db.collection("users").document(userId)
                            .update("friends", FieldValue.arrayUnion(document.id))
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
    } else {
        Toast.makeText(context, R.string.Friend_doesnt_exist, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun FriendsDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit
) {
    var friendNameText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text(text = stringResource(R.string.Friend_Name)) },
        text = {
            OutlinedTextField(
                value = friendNameText,
                onValueChange = { friendNameText = it },
                label = { Text(stringResource(R.string.Friend_Name)) },
                placeholder = { Text(stringResource(R.string.Friend_Name)) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirmation(friendNameText)
            }) {
                Text(stringResource(R.string.Confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(stringResource(R.string.exit))
            }
        }
    )
}
