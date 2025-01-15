package com.example.semestralka

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SetupForm(navController: NavController) {
    val buttonWidth = 150.dp // Šířka tlačítek

    Box(
        modifier = Modifier.fillMaxSize() // Komponenta Box zabírá celou obrazovku
    ) {
        // Zobrazení pozadí pomocí obrázku
        Image(
            painter = painterResource(R.drawable.back_removed), // Obrázek z resources
            contentDescription = "pozadi", // Popis pro čtečky obrazovky
            modifier = Modifier.fillMaxSize(), // Obrázek zabírá celou obrazovku
            contentScale = ContentScale.FillBounds, // Obrázek vyplní celý prostor
            alpha = 0.5f // Nastavení průhlednosti obrázku
        )

        // Sloupec pro text a tlačítka
        Column(
            modifier = Modifier
                .fillMaxWidth() // Sloupec zabírá celou šířku obrazovky
                .fillMaxHeight(), // Sloupec zabírá celou výšku obrazovky
            horizontalAlignment = Alignment.CenterHorizontally, // Zarovnání prvků na střed horizontálně
            verticalArrangement = Arrangement.Center, // Zarovnání prvků na střed vertikálně
        ) {
            // Zobrazení textu
            Text(
                text = stringResource(R.string.rof), // Text získaný z resources
                style = MaterialTheme.typography.titleLarge, // Styl textu
                modifier = Modifier.padding(top = 16.dp, bottom = 148.dp) // Vnější odsazení textu
            )

            // Tlačítko "Play"
            FilledTonalButton(
                onClick = { navController.navigate("playSetup") }, // Akce po kliknutí na tlačítko
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green // Barva pozadí tlačítka
                ),
                modifier = Modifier.width(buttonWidth) // Nastavení šířky tlačítka
            ) {
                Text(stringResource(R.string.play)) // Text tlačítka
            }

            // Tlačítko "Friends"
            FilledTonalButton(
                onClick = { navController.navigate("friends") }, // Akce po kliknutí na tlačítko
                modifier = Modifier.width(buttonWidth) // Nastavení šířky tlačítka
            ) {
                Text(stringResource(R.string.friends)) // Text tlačítka
            }
        }
    }
}
