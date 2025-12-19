package com.example.healthcare.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.healthcare.R

class HealthArticlesActivity : AppCompatActivity() {

    private val health_details = arrayOf(
        arrayOf("Walking Daily", "", "", "", "Click More Details"),
        arrayOf("Home care of COVID-19", "", "", "", "Click More Details"),
        arrayOf("Stop Smoking", "", "", "", "Click More Details"),
        arrayOf("Body Cramps", "", "", "", "Click More Details"),
        arrayOf("Healthy Gut", "", "", "", "Click More Details")
    )
    
    // Updated with specific health images
    private val images = intArrayOf(
        R.drawable.health1,
        R.drawable.health2,
        R.drawable.health3,
        R.drawable.health4,
        R.drawable.health5
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_articles)

        val listView: ListView = findViewById(R.id.listViewHA)
        
        val list = ArrayList<HashMap<String, String>>()
        for (i in health_details.indices) {
            val item = HashMap<String, String>()
            item["line1"] = health_details[i][0]
            item["line2"] = health_details[i][4]
            item["line3"] = i.toString() 
            list.add(item)
        }

        val sa = SimpleAdapter(
            this,
            list,
            R.layout.multi_lines,
            arrayOf("line1", "line2"),
            intArrayOf(R.id.line_a, R.id.line_b)
        )
        listView.adapter = sa

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            val it = Intent(this, HealthArticlesDetailsActivity::class.java)
            it.putExtra("text1", health_details[i][0])
            it.putExtra("imageId", images[i])
            startActivity(it)
        }
    }
}
