package com.example.myapp012aimagetoapp

import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random
import com.example.myapp012aimagetoapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Nastavení view bindingu
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRateImage.setOnClickListener {
            rateImage()
        }
        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
                uri: Uri? -> binding.ivImage.setImageURI(uri)
        }

        binding.btnTakeImage.setOnClickListener {
            getContent.launch("image/*")
        }
    }

    private fun rateImage() {
        val score = Random.nextInt(1, 11)
        val comments = listOf(
            "Trochu víc světla by prospělo!",
            "Mistrovské dílo digitálního věku.",
            "Dobrý pokus!",
            "Wow, 10/10!",
            "Kdo tě to naučil fotit?",
            "Chce to ještě trochu doladit.",
            "Docela dobré!",
            "Skoro jako od profesionála!",
            "Je to umění nebo náhoda?",
            "Epické!"
        )

        val comment = comments[score - 1]
        binding.tvScore.text = "Hodnocení: $score/10 – $comment"
    }
}