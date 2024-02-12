package com.example.pyramid

import android.content.ContentValues
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

@Composable
fun FriendsForm(navController: NavController) {
    val friendsFlow = getUserFriendsFromFirestore().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // List of friends
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(friendsFlow.value) { friend ->
                FriendItem(friend = friend)
            }
        }

        // Add friend button
        IconButton(
            onClick = {
                // Navigate to add friend screen or perform desired action
            },
            modifier = Modifier
                .align(Alignment.End)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Friend")
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

        Log.d(TAG, "Friend IDs: $friendIds")

        for (friendId in friendIds) {
            val friendDoc = db.collection("users").document(friendId).get().await()
            val nickname = friendDoc.getString("nickname")
            Log.d(TAG, "Friend ID: $friendId, Nickname: $nickname")
            nickname?.let {
                friendsList.add(it)
            }
        }

        emit(friendsList)
    } else {
        emit(emptyList())
    }
}


