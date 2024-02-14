package com.example.pyramid

import android.content.ContentValues
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

@Composable
fun PlaySetupForm(navController: NavController) {
    val friendNames = remember { mutableStateOf<List<String>>(emptyList()) }
    val newFriendName = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display existing friends
        friendNames.value.forEachIndexed { index, name ->
            Text(text = "Friend ${index + 1}: $name")
        }

        // Input field for adding new friends
        TextField(
            value = newFriendName.value,
            onValueChange = { newFriendName.value = it },
            label = { Text("Enter friend's name") },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Button to add a new friend
        Button(
            onClick = {
                friendNames.value = friendNames.value + newFriendName.value
                newFriendName.value = ""
            },
            enabled = newFriendName.value.isNotBlank()
        ) {
            Text("Add Friend")
        }

        // Button to proceed to the next step (e.g., start the game)
        Button(
            onClick = {
                    saveFriendListToDatabase(friendList = friendNames.value)
                    navController.navigate("game")
            },
            enabled = friendNames.value.isNotEmpty()
        ) {
            Text("Start Game")
        }
    }
}

private fun saveFriendListToDatabase(friendList: List<String>) {
    val db = Firebase.firestore
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid
    if (userId != null) {
        db.collection("users")
        val userRef = db.collection("users").document(userId)
        userRef.update("singleFriends", friendList)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "Friends added: $userId")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Unknow error", e)
            }
    }
}