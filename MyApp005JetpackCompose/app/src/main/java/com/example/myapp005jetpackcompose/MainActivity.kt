package com.example.myapp005jetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val namesList = remember { mutableStateListOf<String>() }  // Uchování seznamu jmen

    NavHost(navController = navController, startDestination = "form_screen") {
        composable("form_screen") {
            ComposeExample(navController, namesList)
        }
        composable("list_screen") {
            NameListScreen(navController, namesList)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeExample(navController: NavHostController, namesList: MutableList<String>) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }
    var rank by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Moje Aplikace", color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.DarkGray,
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Jméno") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = surname,
                onValueChange = { surname = it },
                label = { Text("Příjmení") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = age,
                onValueChange = {
                    if (it.all { char -> char.isDigit() } && it.toIntOrNull()?.let { it <= 150 } == true) {
                        age = it
                    }
                },
                label = { Text("Věk (hodnota menší než 151)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = place,
                onValueChange = { place = it },
                label = { Text("Bydliště") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = rank,
                onValueChange = { rank = it },
                label = { Text("LoL Rank") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        resultText = "Jmenuji se $name $surname. Je mi $age let a moje bydliště je $place."
                        namesList.add("$name $surname")  // Přidání jména do seznamu
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Odeslat")
                }

                Button(
                    onClick = {
                        name = ""
                        surname = ""
                        age = ""
                        place = ""
                        resultText = ""
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF0000),
                        contentColor = Color.White
                    )
                ) {
                    Text("Vymazat")
                }
            }

            // Zobrazení výsledku a tlačítko pro přechod na seznam jmen
            if (resultText.isNotEmpty()) {
                Text(
                    text = resultText,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            Button(
                onClick = {
                    navController.navigate("list_screen")  // Přechod na seznam jmen
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Zobrazit seznam")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameListScreen(navController: NavHostController, namesList: MutableList<String>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seznam Jmen", color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.DarkGray,
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (namesList.isEmpty()) {
                Text("Seznam je prázdný.")
            } else {
                namesList.forEach { name ->
                    Text(text = name)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tlačítko pro vymazání seznamu
            Button(
                onClick = {
                    namesList.clear()  // Vymazání seznamu
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF0000),
                    contentColor = Color.White
                )
            ) {
                Text("Vymazat seznam")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tlačítko zpět
            Button(
                onClick = {
                    navController.popBackStack()  // Návrat na předchozí obrazovku
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Zpět")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainScreen()
}
