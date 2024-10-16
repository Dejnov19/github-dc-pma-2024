package com.example.myapp006moreactivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapp006moreactivities.databinding.ActivityPlayerAddedBinding

class PlayerAddedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerAddedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerAddedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nickname = intent.getStringExtra("nickname")
        binding.successMessage.text = "Hráč $nickname úspěšně přidán!"

        binding.backButton.setOnClickListener {
            finish() // Vrátí se zpět na první aktivitu
        }

        binding.showListButton.setOnClickListener {
            // Přejde na třetí aktivitu se seznamem hráčů
            val intent = Intent(this, PlayerListActivity::class.java)
            startActivity(intent)
        }
    }
}
