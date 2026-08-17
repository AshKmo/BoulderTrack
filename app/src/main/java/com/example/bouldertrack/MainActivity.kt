package com.example.bouldertrack

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var state: BoulderTrackState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // if there is no previous bouldering state, create a new state, or otherwise load the existing state from the Bundle
        // the deprecated variant of getParcelable is being used here because the new variant is only available in API level 33
        state = if (savedInstanceState == null)
            BoulderTrackState()
        else {
            Log.i(this::class.simpleName, "Loading instance state")

            savedInstanceState.getParcelable("boulderTrackState")!!
        }
    }

    // when the application's state needs to be saved, save the current bouldering state
    override fun onSaveInstanceState(outState: Bundle) {
        Log.i(this::class.simpleName, "Saving instance state")

        outState.putParcelable("boulderTrackState", state)

        // allow the superclass to save its state too
        super.onSaveInstanceState(outState)
    }
}