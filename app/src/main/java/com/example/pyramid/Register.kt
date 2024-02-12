package com.example.pyramid

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.graphics.Color
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
    var emailText by remember { mutableStateOf("") }
    var nicknameText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    val buttonWidth = 150.dp
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        OutlinedTextField(
            value = emailText,
            onValueChange = { emailText = it },
            label = { Text("Email") },
            placeholder = { Text("Enter your email") },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null)
            },
            modifier = Modifier.fillMaxWidth().padding(20.dp)
        )

        OutlinedTextField(
            value = nicknameText,
            onValueChange = { nicknameText = it },
            label = { Text("Nickname") },
            placeholder = { Text("Enter your nickname") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            trailingIcon = { Icon(Icons.Default.Clear, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().padding(20.dp)
        )

        OutlinedTextField(
            value = passwordText,
            onValueChange = { passwordText = it },
            label = { Text("Password") },
            placeholder = { Text("Enter your password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = { Icon(Icons.Default.Clear, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().padding(20.dp)
        )

        FilledTonalButton(
            onClick = {
                registerUser(emailText,nicknameText,passwordText,navController,context)
                      },
            modifier = Modifier
                .width(buttonWidth)
        ) {
            Text(stringResource(R.string.register))
        }
    }
}

private fun registerUser(email: String, nickname: String, password: String,navController: NavController, context: Context) {
    if (email.isBlank() || nickname.isBlank() || password.isBlank()) {
        Toast.makeText(context, "Credencials cannot be empty", Toast.LENGTH_SHORT).show()
        return
    }
    val auth = FirebaseAuth.getInstance()
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(nickname)
                    .build()
                user?.updateProfile(profileUpdates)
                    ?.addOnCompleteListener { profileTask ->
                        if (profileTask.isSuccessful) {
                            Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
                            addToDB(email, nickname, password)
                            navController.navigate("setup")
                        } else {
                            Log.w(TAG, "Failed to update user profile", profileTask.exception)
                            Toast.makeText(context, "Failed to update user profile", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
    Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
}
}

private fun addToDB(email: String, nickname: String, password: String) {
    val db = Firebase.firestore
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid
    if (userId != null) {
        val user = hashMapOf(
            "email" to email,
            "nickname" to nickname,
            "friends" to listOf<String>()
        )
        db.collection("users")
            .document(userId)
            .set(user)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added with ID: $userId")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
}




