package com.supdevinci.quizz.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.widget.TextView
import android.widget.LinearLayout
import android.view.Gravity

class LeaderboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }

        val textView = TextView(this).apply {
            text = "Bienvenue sur la deuxième activité"
            textSize = 20f
        }

        layout.addView(textView)
        setContentView(layout)
    }
}
