package com.example.myapp002calculator

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Získání referencí na prvky v layoutu
        val firstNumber = findViewById<EditText>(R.id.firstNumber)
        val secondNumber = findViewById<EditText>(R.id.secondNumber)
        val addButton = findViewById<Button>(R.id.addButton)
        val subtractButton = findViewById<Button>(R.id.subtractButton)
        val multiplyButton = findViewById<Button>(R.id.multiplyButton)
        val divideButton = findViewById<Button>(R.id.divideButton)
        val resultTextView = findViewById<TextView>(R.id.resultTextView)

        // Funkce pro sčítání
        addButton.setOnClickListener {
            val num1 = firstNumber.text.toString().toDoubleOrNull()
            val num2 = secondNumber.text.toString().toDoubleOrNull()

            if (num1 != null && num2 != null) {
                val result = num1 + num2
                resultTextView.text = "Výsledek: $result"
            } else {
                resultTextView.text = "Prosím, zadej platná čísla."
            }
        }

        // Funkce pro odčítání
        subtractButton.setOnClickListener {
            val num1 = firstNumber.text.toString().toDoubleOrNull()
            val num2 = secondNumber.text.toString().toDoubleOrNull()

            if (num1 != null && num2 != null) {
                val result = num1 - num2
                resultTextView.text = "Výsledek: $result"
            } else {
                resultTextView.text = "Prosím, zadej platná čísla."
            }
        }

        // Funkce pro násobení
        multiplyButton.setOnClickListener {
            val num1 = firstNumber.text.toString().toDoubleOrNull()
            val num2 = secondNumber.text.toString().toDoubleOrNull()

            if (num1 != null && num2 != null) {
                val result = num1 * num2
                resultTextView.text = "Výsledek: $result"
            } else {
                resultTextView.text = "Prosím, zadej platná čísla."
            }
        }

        // Funkce pro dělení
        divideButton.setOnClickListener {
            val num1 = firstNumber.text.toString().toDoubleOrNull()
            val num2 = secondNumber.text.toString().toDoubleOrNull()

            if (num1 != null && num2 != null) {
                if (num2 != 0.0) {
                    val result = num1 / num2
                    resultTextView.text = "Výsledek: $result"
                } else {
                    resultTextView.text = "Dělení nulou není možné."
                }
            } else {
                resultTextView.text = "Prosím, zadej platná čísla."
            }
        }
    }
}
