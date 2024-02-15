package com.ringoffire.pyramid


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
import androidx.compose.ui.res.stringResource
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
        friendNames.value.forEachIndexed { index, name ->
            Text(text = "${index + 1}: $name")
        }
        TextField(
            value = newFriendName.value,
            onValueChange = { newFriendName.value = it },
            label = { Text(stringResource(R.string.Friend_Name)) },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Button(
            onClick = {
                friendNames.value = friendNames.value + newFriendName.value
                newFriendName.value = ""
            },
            enabled = newFriendName.value.isNotBlank()
        ) {
            Text(stringResource(R.string.Friend_add))
        }

        Button(
            onClick = {
                    saveFriendListToDatabase(friendList = friendNames.value)
                    navController.navigate("game")
            },
            enabled = friendNames.value.isNotEmpty()
        ) {
            Text(stringResource(R.string.Start_game))
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
            }
            .addOnFailureListener {
            }
    }
}