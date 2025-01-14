package com.example.myapp007toastsnackbar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapp007toastsnackbar.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

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

                // Zobrazení Snackbaru s potvrzením
                Snackbar.make(binding.root, "Hráč $nickname byl přidán do seznamu", Snackbar.LENGTH_SHORT).show()

                // Vyčistí vstupní pole po přidání hráče
                binding.nicknameInput.text.clear()
                binding.divisionInput.text.clear()
            } else {
                // Zobrazení Snackbaru pro upozornění na prázdné pole
                Snackbar.make(binding.root, "Vyplňte prosím obě pole", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.showListButton.setOnClickListener {
            // Spustíme třetí aktivitu, kde se zobrazí seznam hráčů
            val intent = Intent(this, PlayerListActivity::class.java)
            startActivity(intent)
        }
    }
}