package com.example.myapp016avanocniappka

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapp016avanocniappka.ui.theme.MyApp016aVanocniAppkaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp016aVanocniAppkaTheme {
                AdventCalendarApp()
            }
        }
    }
}


@Preview
@Composable
fun AdventCalendarApp() {
    // Stav pro otevřené dny
    val openedDays = remember { mutableStateListOf<Int>() }
    val images = listOf(
        R.drawable.chillguy1, R.drawable.chillguy2, R.drawable.chillguy4, R.drawable.chillguy5,
        R.drawable.chillguy6, R.drawable.chillguy7, R.drawable.chillguy8,
        R.drawable.chillguy16, R.drawable.chillguy17, R.drawable.chillguy18,
        R.drawable.chillguy19, R.drawable.chillguy20, R.drawable.chillguy21,
        R.drawable.chillguy22, R.drawable.chillguy23, R.drawable.chillguy24
    )
    var fullScreenImage by remember { mutableStateOf<Int?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
                .background(Color.White)
        ) {
            Text(
                text = "Adventní chill Kalendář",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            Button(
                onClick = { openedDays.clear() },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Reset Kalendáře")
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.padding(8.dp),
                content = {
                    items(24) { day ->
                        AdventDay(
                            day = day + 1,
                            isOpen = openedDays.contains(day + 1),
                            onClick = {
                                if (!openedDays.contains(day + 1)) {
                                    openedDays.add(day + 1)
                                }
                            },
                            onImageClick = { fullScreenImage = it },
                            imageRes = images[day % images.size]
                        )
                    }
                }
            )
        }

        // Zobrazení obrázku na celé obrazovce
        fullScreenImage?.let { imageRes ->
            FullScreenImage(imageRes = imageRes) {
                fullScreenImage = null
            }
        }
    }
}

@Composable
fun AdventDay(day: Int, isOpen: Boolean, onClick: () -> Unit, onImageClick: (Int) -> Unit, imageRes: Int) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .size(80.dp)
            .clickable(enabled = !isOpen, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isOpen) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Image for day $day",
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onImageClick(imageRes) } // Kliknutí pro zvětšení obrázku
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color.Red, Color.Green),
                            start = Offset.Zero,
                            end = Offset.Infinite
                        )
                    )
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Text(
                    text = "$day",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
fun FullScreenImage(imageRes: Int, onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { onClose() }, // Zavření obrázku při kliknutí
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Full Screen Image",
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        )
        Text(
            text = "Klikni pro zavření",
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}
