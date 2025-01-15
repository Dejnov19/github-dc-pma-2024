package com.example.semestralka

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.semestralka.ui.theme.SemestralkaTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Povolení režimu "Edge to Edge", který umožňuje aplikaci využít celou obrazovku
        enableEdgeToEdge()

        // Nastavení obsahu obrazovky
        setContent {
            SemestralkaTheme {
                // Vytvoření instance navigačního kontroléru
                val navController = rememberNavController()

                // Definice povrchu aplikace s pozadím
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                }

                // Nastavení navigace v aplikaci s jednotlivými obrazovkami
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") { LoginForm(navController, this@MainActivity) }
                    composable("log") { LogForm(navController) }
                    composable("setup") { SetupForm(navController) }
                    composable("playSetup") { PlaySetupForm(navController) }
                    composable("game") { GameForm(navController) }
                    composable("friends") { FriendsForm(navController) }
                    composable("register") { RegisterForm(navController) }
                }
            }
        }
    }
}

@Composable
fun LoginForm(navController: NavController, activity: ComponentActivity) {
    // Definice pevné šířky tlačítek
    val buttonWidth = 150.dp

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Zobrazení pozadí obrazovky
        Image(
            painter = painterResource(R.drawable.back_removed),
            contentDescription = "pozadi",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
            alpha = 0.5f // Nastavení průhlednosti obrázku
        )

        // Rozvržení obsahu do sloupce
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Text s názvem nebo mottem aplikace
            Text(
                text = stringResource(R.string.rof),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 16.dp, bottom = 148.dp)
            )

            // Tlačítko pro přihlášení
            FilledTonalButton(
                onClick = { navController.navigate("log") },
                modifier = Modifier.width(buttonWidth)
            ) {
                Text(stringResource(R.string.login))
            }

            // Tlačítko pro registraci
            FilledTonalButton(
                onClick = { navController.navigate("register") },
                modifier = Modifier.width(buttonWidth)
            ) {
                Text(stringResource(R.string.register))
            }

            // Tlačítko pro ukončení aplikace
            FilledTonalButton(
                onClick = { activity.finish() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red // Barva tlačítka
                ),
                modifier = Modifier.width(buttonWidth)
            ) {
                Text(stringResource(R.string.exit))
            }
        }
    }
}
