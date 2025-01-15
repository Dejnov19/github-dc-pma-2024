package com.example.semestralka

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Funkce zobrazuje hru s náhodně zamíchanými kartami a střídáním hráčů
@Composable
fun GameForm(navController: NavController) {
    // Seznam obrázků karet, které se náhodně zamíchají
    val cardImages = listOf(
        // Karty - esa
        R.drawable.ace_of_diamonds,
        R.drawable.ace_of_hearts,
        R.drawable.ace_of_clubs,
        R.drawable.ace_of_spades,
        // Karty - králové
        R.drawable.king_of_diamonds,
        R.drawable.king_of_hearts,
        R.drawable.king_of_clubs,
        R.drawable.king_of_spades,
        // Karty - dámy
        R.drawable.queen_of_clubs,
        R.drawable.queen_of_diamonds,
        R.drawable.queen_of_hearts,
        R.drawable.queen_of_spades,
        // Další karty
        R.drawable.jack_of_clubs,
        R.drawable.jack_of_diamonds,
        // ... (ostatní karty)
    ).shuffled() // Zamíchání karet

    // Mapování obrázků karet na jejich názvy/pravidla
    val cardNames = remember {
        mapOf(
            R.drawable.ace_of_diamonds to "Waterfall", // Vodopád
            R.drawable.king_of_diamonds to "Kings cup", // Královský pohár
            R.drawable.queen_of_clubs to "Question master", // Pán otázek
            R.drawable.jack_of_clubs to "Thumb", // Palec
            // ... (ostatní mapování)
        )
    }

    // Celkový počet karet
    val totalCards = cardImages.size
    val currentImageIndex = remember { mutableIntStateOf(0) } // Index aktuální karty

    // Seznam přátel uživatele, který se načítá z Firebase
    val friendList = remember { mutableStateOf<List<String>>(emptyList()) }
    val currentFriendIndex = remember { mutableIntStateOf(0) } // Index aktuálního hráče

    // Načtení seznamu přátel při spuštění composable
    LaunchedEffect(key1 = "fetchSingleFriend") {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            FirebaseFirestore.getInstance().collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    val data = document.data
                    val friends = data?.get("singleFriends") as? List<String>
                    if (friends != null) {
                        friendList.value = friends // Seznam přátel z databáze
                    }
                }
                .addOnFailureListener {
                    // Chyba při načítání seznamu přátel
                }
        }
    }

    // Rozložení obsahu na obrazovce
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Zobrazení aktuálního hráče
        Text(
            text = friendList.value.getOrNull(currentFriendIndex.intValue)
                ?: stringResource(R.string.loading), // Pokud není načteno, zobrazí "Načítám..."
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Zobrazení názvu aktuální karty
        getCardName(cardImages[currentImageIndex.intValue], cardNames)?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Zobrazení obrázku aktuální karty
        Image(
            painter = painterResource(id = cardImages[currentImageIndex.intValue]),
            contentDescription = "Actual card",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Zobrazení počítadla karet
        Text("Card ${currentImageIndex.intValue + 1}/$totalCards")

        Spacer(modifier = Modifier.height(16.dp))

        // Tlačítko pro přechod na další kartu a hráče
        Button(onClick = {
            currentImageIndex.intValue = (currentImageIndex.intValue + 1) % totalCards
            currentFriendIndex.intValue = (currentFriendIndex.intValue + 1) % friendList.value.size
        }) {
            Text(stringResource(R.string.Next_Card))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tlačítko pro návrat do hlavního menu
        Button(onClick = {
            navController.navigate("setup")
        }) {
            Text(stringResource(R.string.exit))
        }
    }
}

// Funkce pro získání názvu karty podle jejího ID
fun getCardName(imageResourceId: Int, cardNames: Map<Int, String>): String? {
    return cardNames[imageResourceId]
}
