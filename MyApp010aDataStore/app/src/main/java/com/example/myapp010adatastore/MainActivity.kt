package com.example.myapp010adatastore

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import com.example.myapp010adatastore.databinding.ActivityMainBinding // Import View Binding
import java.util.prefs.Preferences

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        private val NAME_KEY = stringPreferencesKey("user_name")
        private val AGE_KEY = intPreferencesKey("user_age")
        private val IS_ADULT_KEY = booleanPreferencesKey("is_adult")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Uložení dat při kliknutí na tlačítko
        binding.saveButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val age = binding.ageEditText.text.toString().toIntOrNull() ?: 0
            val isAdult = binding.adultCheckBox.isChecked
            saveData(name, age, isAdult)
            Toast.makeText(this, "Data uložena", Toast.LENGTH_SHORT).show()
        }

        // Načtení dat při kliknutí na tlačítko
        binding.loadButton.setOnClickListener {
            loadData()
            Toast.makeText(this, "Data načtena", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveData(name: String, age: Int, isAdult: Boolean) {
        runBlocking {
            applicationContext.dataStore.edit { preferences ->
                preferences[NAME_KEY] = name
                preferences[AGE_KEY] = age
                preferences[IS_ADULT_KEY] = isAdult
            }
        }
    }

    private fun loadData() {
        runBlocking {
            val preferences = applicationContext.dataStore.data.first()
            val name = preferences[NAME_KEY] ?: ""
            val age = preferences[AGE_KEY] ?: 0
            val isAdult = preferences[IS_ADULT_KEY] ?: false

            // Aktualizace UI
            binding.nameEditText.setText(name)
            binding.ageEditText.setText(age.toString())
            binding.adultCheckBox.isChecked = isAdult
        }
    }
}