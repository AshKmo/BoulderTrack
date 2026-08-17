package com.example.bouldertrack

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        for (buttonId in arrayOf(
            R.id.resetButton,
            R.id.fallButton,
            R.id.chalkUpButton,
            R.id.gripButton
        )) {
            val button: Button = findViewById(buttonId)
            button.setOnClickListener {
                Log.i(this::class.simpleName, "Button labelled '${button.text.toString()}' pushed")
            }
        }
    }
}