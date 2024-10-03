package com.example.myapp004objednavka

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp004objednavka.databinding.ActivityOrderBinding

class OrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate layout with view binding
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the order summary passed from MainActivity
        val orderSummary = intent.getStringExtra("ORDER_SUMMARY")

        // Display the order summary in the TextView
        binding.textViewOrderDetails.text = orderSummary
    }
}
