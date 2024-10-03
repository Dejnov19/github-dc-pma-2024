package com.example.myapp004objednavka

import android.os.Bundle
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp004objednavka.databinding.ActivityMainBinding
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set listeners for pizza selection
        binding.radioGroupPizza.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioPizza1 -> binding.imageViewPizza.setImageResource(R.drawable.ctyri_druhy_pizza)
                R.id.radioPizza2 -> binding.imageViewPizza.setImageResource(R.drawable.hawai_pizza)
                R.id.radioPizza3 -> binding.imageViewPizza.setImageResource(R.drawable.sunkova_pizza)
            }
        }

        // Handle order button click
        binding.buttonOrder.setOnClickListener {
            val selectedPizza = when (binding.radioGroupPizza.checkedRadioButtonId) {
                R.id.radioPizza1 -> "Margarita"
                R.id.radioPizza2 -> "Pepperoni"
                R.id.radioPizza3 -> "Hawaiian"
                else -> "No pizza selected"
            }

            val extras = mutableListOf<String>()
            if (binding.checkBoxCheese.isChecked) extras.add("Extra sýrová")
            if (binding.checkBoxSpicy.isChecked) extras.add("Chilli koření")
            if (binding.checkBoxGlutenFree.isChecked) extras.add("Bezlepková")

            // Show the selected pizza and extras (you can display this in a TextView, Toast, etc.)
            val orderSummary = "Objednáno: $selectedPizza s doplňky: ${extras.joinToString()}"
            // Example: display as a Toast
            Toast.makeText(this, orderSummary, Toast.LENGTH_LONG).show()
        }
    }
}
