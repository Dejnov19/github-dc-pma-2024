package com.example.myapp006moreactivities

import android.app.AlertDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp006moreactivities.databinding.ActivityPlayerListBinding
import android.content.Intent

class PlayerListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerListBinding
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Získáme seznam hráčů ve formátu: "Nickname - Division"
        val playerInfoList = MainActivity.playerList.map { "${it.first} - ${it.second}" }.toMutableList()

        // Vytvoříme adapter a naplníme ListView
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, playerInfoList)
        binding.playerListView.adapter = adapter

        // Nastavení akce pro tlačítko Zpět
        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish() // Vrátí se na předchozí aktivitu
        }

        // Nastavení dlouhého kliknutí na položku seznamu
        binding.playerListView.setOnItemLongClickListener { _, _, position, _ ->
            val selectedPlayer = playerInfoList[position]

            // Zobrazení dialogového okna pro potvrzení smazání
            AlertDialog.Builder(this)
                .setTitle("Smazat hráče")
                .setMessage("Opravdu chcete smazat hráče $selectedPlayer?")
                .setPositiveButton("Ano") { _, _ ->
                    // Smazání hráče ze seznamu a aktualizace
                    MainActivity.playerList.removeAt(position)
                    playerInfoList.removeAt(position)
                    adapter.notifyDataSetChanged()

                    Toast.makeText(this, "Hráč smazán", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Ne", null)
                .show()

            true
        }
    }
}
