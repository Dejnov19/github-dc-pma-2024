package com.example.myapp006moreactivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapp006moreactivities.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Statická proměnná pro uchování seznamu hráčů
    companion object {
        val playerList = mutableListOf<Pair<String, String>>() // (Nickname, Division)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addPlayerButton.setOnClickListener {
            val nickname = binding.nicknameInput.text.toString()
            val division = binding.divisionInput.text.toString()

            if (nickname.isNotEmpty() && division.isNotEmpty()) {
                // Přidáme hráče do seznamu
                playerList.add(Pair(nickname, division))

                // Spustíme druhou aktivitu a pošleme tam informace
                val intent = Intent(this, PlayerAddedActivity::class.java)
                intent.putExtra("nickname", nickname)
                startActivity(intent)
            }
        }

        binding.showListButton.setOnClickListener {
            // Spustíme třetí aktivitu, kde se zobrazí seznam hráčů
            val intent = Intent(this, PlayerListActivity::class.java)
            startActivity(intent)
        }
    }
}