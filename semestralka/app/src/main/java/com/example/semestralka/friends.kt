package com.example.semestralka

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
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
    val friendsFlow = getUserFriendsFromFirestore().collectAsState(initial = emptyList())
    var isDialogOpen by remember { mutableStateOf(false) }
    val context = LocalContext.current



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(friendsFlow.value) { friend ->
                FriendItem(friend = friend)
            }
        }

        IconButton(
            onClick = {
                isDialogOpen = true
            },
            modifier = Modifier
                .align(Alignment.End)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Friend")
        }

        if (isDialogOpen) {
            FriendsDialog(
                onDismissRequest = {
                    isDialogOpen = false
                }
            ) { friendName -> //on cofrim
                addFriendToDatabase(friendName,context)
                isDialogOpen = false
            }
        }
    }
}




@Composable
fun FriendItem(friend: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 8.dp)
    ) {
        Icon(Icons.Default.Person, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = friend)
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


fun addFriendToDatabase(friendName: String, context: Context) {
    val db = Firebase.firestore
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid
    if (userId != null) {

        db.collection("users")
            .whereEqualTo("nickname", friendName)
            .get().addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {

                    db.collection("users").document(userId)
                        .update("friends", FieldValue.arrayUnion(document.id))
                        .addOnSuccessListener {
                            Toast.makeText(context, R.string.friend_added, Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, R.string.Friend_doesnt_exist, Toast.LENGTH_SHORT).show()

                        }
                }
            }.addOnFailureListener { e ->
                Toast.makeText(context, R.string.Friend_doesnt_exist, Toast.LENGTH_SHORT).show()
            }




    }
}
@Composable
fun FriendsDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit
) {
    var friendNameText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                OutlinedTextField(
                    value = friendNameText,
                    onValueChange = { friendNameText = it },
                    label = { R.string.Friend_Name },
                    placeholder = { R.string.Friend_Name },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text(stringResource(R.string.exit))
                    }
                    TextButton(
                        onClick = { onConfirmation(friendNameText) },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text(stringResource(R.string.Confirm))
                    }
                }
            }
        }
    }
}



