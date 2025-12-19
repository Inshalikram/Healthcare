package com.example.healthcare.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.healthcare.R

class HealthArticlesDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_articles_details)

        val tvTitle: TextView = findViewById(R.id.textViewHADTitle)
        val img: ImageView = findViewById(R.id.imageViewHAD)

        val intent = intent
        val title = intent.getStringExtra("text1")
        val imageId = intent.getIntExtra("imageId", 0)

        tvTitle.text = title
        if (imageId != 0) {
            img.setImageResource(imageId)
        }
    }
}
