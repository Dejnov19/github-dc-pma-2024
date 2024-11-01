package com.example.myapp007toastsnackbar

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp007toastsnackbar.databinding.ActivityPlayerListBinding
import android.content.Intent

class PlayerListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerListBinding
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val playerInfoList = MainActivity.playerList.map { "${it.first} - ${it.second}" }.toMutableList()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, playerInfoList)
        binding.playerListView.adapter = adapter

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        binding.playerListView.setOnItemLongClickListener { _, _, position, _ ->
            val selectedPlayer = playerInfoList[position]

            AlertDialog.Builder(this)
                .setTitle("Smazat hráče")
                .setMessage("Opravdu chcete smazat hráče $selectedPlayer?")
                .setPositiveButton("Ano") { _, _ ->
                    MainActivity.playerList.removeAt(position)
                    playerInfoList.removeAt(position)
                    adapter.notifyDataSetChanged()

                    showCustomToast("Hráč smazán")
                }
                .setNegativeButton("Ne", null)
                .show()

            true
        }
    }

    private fun showCustomToast(message: String) {
        val layoutInflater = layoutInflater
        val customToastLayout: View = layoutInflater.inflate(R.layout.custom_toast, null)
        val toastText: TextView = customToastLayout.findViewById(R.id.toast_text)
        toastText.text = message

        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = customToastLayout
        toast.show()
    }
}
