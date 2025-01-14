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

// Seznam obrázků karet, které se náhodně zamíchají
@Composable
fun GameForm(navController: NavController) {
    val cardImages = listOf(
        R.drawable.ace_of_diamonds,
        R.drawable.ace_of_hearts,
        R.drawable.ace_of_clubs,
        R.drawable.ace_of_spades,

        R.drawable.king_of_diamonds,
        R.drawable.king_of_hearts,
        R.drawable.king_of_clubs,
        R.drawable.king_of_spades,

        R.drawable.queen_of_clubs,
        R.drawable.queen_of_diamonds,
        R.drawable.queen_of_hearts,
        R.drawable.queen_of_spades,

        R.drawable.jack_of_clubs,
        R.drawable.jack_of_diamonds,
        R.drawable.jack_of_hearts,
        R.drawable.jack_of_spades,

        R.drawable.ten_of_clubs,
        R.drawable.ten_of_diamonds,
        R.drawable.ten_of_hearts,
        R.drawable.ten_of_spades,

        R.drawable.nine_of_clubs,
        R.drawable.nine_of_diamonds,
        R.drawable.nine_of_hearts,
        R.drawable.nine_of_spades,

        R.drawable.eight_of_clubs,
        R.drawable.eight_of_diamonds,
        R.drawable.eight_of_hearts,
        R.drawable.eight_of_spades,

        R.drawable.seven_of_clubs,
        R.drawable.seven_of_diamonds,
        R.drawable.seven_of_hearts,
        R.drawable.seven_of_spades,

        R.drawable.six_of_clubs,
        R.drawable.six_of_diamonds,
        R.drawable.six_of_hearts,
        R.drawable.six_of_spades,

        R.drawable.five_of_clubs,
        R.drawable.five_of_diamonds,
        R.drawable.five_of_hearts,
        R.drawable.five_of_spades,

        R.drawable.four_of_clubs,
        R.drawable.four_of_diamonds,
        R.drawable.four_of_hearts,
        R.drawable.four_of_spades,

        R.drawable.three_of_clubs,
        R.drawable.three_of_diamonds,
        R.drawable.three_of_hearts,
        R.drawable.three_of_spades,

        R.drawable.two_of_clubs,
        R.drawable.two_of_diamonds,
        R.drawable.two_of_hearts,
        R.drawable.two_of_spades,

        ).shuffled()

    // Mapování obrázků karet na jejich popisky
    val cardNames = remember {
        mapOf(
            R.drawable.ace_of_diamonds to "Waterfall",
            R.drawable.ace_of_hearts to "Waterfall",
            R.drawable.ace_of_clubs to "Waterfall",
            R.drawable.ace_of_spades to "Waterfall",

            R.drawable.king_of_diamonds to "Kings cup",
            R.drawable.king_of_hearts to "Kings cup",
            R.drawable.king_of_clubs to "Kings cup",
            R.drawable.king_of_spades to "Kings cup",

            R.drawable.queen_of_clubs to "Question master",
            R.drawable.queen_of_diamonds to "Question master",
            R.drawable.queen_of_hearts to "Question master",
            R.drawable.queen_of_spades to "Question master",

            R.drawable.jack_of_clubs to "Thumb",
            R.drawable.jack_of_diamonds to "Thumb",
            R.drawable.jack_of_hearts to "Thumb",
            R.drawable.jack_of_spades to "Thumb",

            R.drawable.ten_of_clubs to "New Rule",
            R.drawable.ten_of_diamonds to "New Rule",
            R.drawable.ten_of_hearts to "New Rule",
            R.drawable.ten_of_spades to "New Rule",

            R.drawable.nine_of_clubs to "Nine rhymes",
            R.drawable.nine_of_diamonds to "Nine rhymes",
            R.drawable.nine_of_hearts to "Nine rhymes",
            R.drawable.nine_of_spades to "Nine rhymes",

            R.drawable.eight_of_clubs to "Eight mate",
            R.drawable.eight_of_diamonds to "Eight mate",
            R.drawable.eight_of_hearts to "Eight mate",
            R.drawable.eight_of_spades to "Eight mate",

            R.drawable.seven_of_clubs to "Seven heaven",
            R.drawable.seven_of_diamonds to "Seven heaven",
            R.drawable.seven_of_hearts to "Seven heaven",
            R.drawable.seven_of_spades to "Seven heaven",

            R.drawable.six_of_clubs to "Six chicks",
            R.drawable.six_of_diamonds to "Six chicks",
            R.drawable.six_of_hearts to "Six chicks",
            R.drawable.six_of_spades to "Six chicks",

            R.drawable.five_of_clubs to "Five guys",
            R.drawable.five_of_diamonds to "Five guys",
            R.drawable.five_of_hearts to "Five guys",
            R.drawable.five_of_spades to "Five guys",

            R.drawable.four_of_clubs to "Four floor",
            R.drawable.four_of_diamonds to "Four floor",
            R.drawable.four_of_hearts to "Four floor",
            R.drawable.four_of_spades to "Four floor",

            R.drawable.three_of_clubs to "Three ME",
            R.drawable.three_of_diamonds to "Three ME",
            R.drawable.three_of_hearts to "Three ME",
            R.drawable.three_of_spades to "Three ME",

            R.drawable.two_of_clubs to "Two YOU",
            R.drawable.two_of_diamonds to "Two YOU",
            R.drawable.two_of_hearts to "Two YOU",
            R.drawable.two_of_spades to "Two YOU",
        )
    }

    // Celkový počet karet
    val totalCards = cardImages.size
    val currentImageIndex = remember { mutableIntStateOf(0) }

    // Seznam přátel uživatele, načítá se z Firebase
    val friendList = remember { mutableStateOf<List<String>>(emptyList()) }
    // Index aktuálního přítele
    val currentFriendIndex = remember { mutableIntStateOf(0) }

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
                        friendList.value = friends
                    }
                }
                .addOnFailureListener {
                } // Zpracování chyby při načítání
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
        // Zobrazení aktuálního frienda
        Text(
            text = friendList.value.getOrNull(currentFriendIndex.intValue) ?: stringResource(R.string.loading),
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
            modifier = Modifier
                .size(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Zobrazení počítadla karet
        Text("Card ${currentImageIndex.intValue + 1}/$totalCards")
        Spacer(modifier = Modifier.height(16.dp))
        // Tlačítko pro přechod na další kartu a přítele
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
