package com.example.myapp007toastsnackbar

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp007toastsnackbar.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toastButton.setOnClickListener {
            val playerName = binding.playerNameInput.text.toString()
            if (playerName.isEmpty()) {
                showCustomToast("Zadejte jméno hráče!", R.drawable.ic_warning)
            } else {
                showCustomToast("Hráč $playerName je registrován!", R.drawable.ic_check)
            }
        }

        binding.snackbarButton.setOnClickListener {
            val snackbar = Snackbar.make(it, "Chcete potvrdit registraci?", Snackbar.LENGTH_LONG)
            snackbar.setAction("Potvrdit") {
                showCustomToast("Registrace potvrzena!", R.drawable.ic_check)
            }
            snackbar.show()
        }
    }

    // Funkce pro zobrazení vlastního Toastu
    private fun showCustomToast(message: String, iconRes: Int) {
        // Naplnění vlastního layoutu
        val inflater: LayoutInflater = layoutInflater
        val layout: View = inflater.inflate(R.layout.custom_toast, null)

        // Nastavení ikonky a textu
        val toastIcon: ImageView = layout.findViewById(R.id.toastIcon)
        val toastMessage: TextView = layout.findViewById(R.id.toastMessage)
        toastIcon.setImageResource(iconRes)
        toastMessage.text = message

        // Vytvoření a zobrazení Toastu
        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }
}
