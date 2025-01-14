package com.example.myapp009asharedpreferences

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editTextName = findViewById<EditText>(R.id.editTextName)
        val editTextAge = findViewById<EditText>(R.id.editTextAge)
        val checkBoxAdult = findViewById<CheckBox>(R.id.checkBoxAdult)
        val buttonSave = findViewById<Button>(R.id.buttonSave)
        val buttonLoad = findViewById<Button>(R.id.buttonLoad)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        buttonSave.setOnClickListener {
            val name = editTextName.text.toString()
            val age = editTextAge.text.toString()
            val isAdult = checkBoxAdult.isChecked

            with(sharedPreferences.edit()) {
                putString("name", name)
                putString("age", age)
                putBoolean("isAdult", isAdult)
                apply()
            }

            Toast.makeText(this, "Data uložena", Toast.LENGTH_SHORT).show()
        }

        buttonLoad.setOnClickListener {
            val name = sharedPreferences.getString("name", "")
            val age = sharedPreferences.getString("age", "")
            val isAdult = sharedPreferences.getBoolean("isAdult", false)

            editTextName.setText(name)
            editTextAge.setText(age)
            checkBoxAdult.isChecked = isAdult

            Toast.makeText(this, "Data načtena", Toast.LENGTH_SHORT).show()
        }
    }
}