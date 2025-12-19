package com.example.healthcare.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.healthcare.R

class LabTestActivity : AppCompatActivity() {

    private val packages = arrayOf(
        arrayOf("Package 1 : Full Body Checkup", "999"),
        arrayOf("Package 2 : Blood Glucose Fasting", "299"),
        arrayOf("Package 3 : COVID-19 Antibody", "499"),
        arrayOf("Package 4 : Thyroid Check", "899"),
        arrayOf("Package 5 : Immunity Check", "699")
    )
    
    private val package_details = arrayOf(
        "Blood Glucose Fasting\nComplete Hemogram\nHbA1c\nIron Studies\nKidney Function Test\nLDH Lactate Dehydrogenase, Serum\nLipid Profile\nLiver Function Test",
        "Blood Glucose Fasting",
        "COVID-19 Antibody - IgG",
        "Thyroid Profile-Total (T3, T4 & TSH Ultra-sensitive)",
        "Complete Hemogram\nIron Studies\nKidney Function Test\nLDH Lactate Dehydrogenase, Serum\nLipid Profile\nLiver Function Test"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab_test)

        val listView: ListView = findViewById(R.id.listViewLT)
        
        val list = ArrayList<HashMap<String, String>>()
        for (i in packages.indices) {
            val item = HashMap<String, String>()
            item["line1"] = packages[i][0]
            item["line2"] = "Total Cost : " + packages[i][1] + "/-"
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
            val it = Intent(this, LabTestDetailsActivity::class.java)
            it.putExtra("text1", packages[i][0])
            it.putExtra("text2", packages[i][1])
            it.putExtra("text3", package_details[i])
            startActivity(it)
        }
    }
}
