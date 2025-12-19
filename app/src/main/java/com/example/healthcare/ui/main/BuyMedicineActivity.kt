package com.example.healthcare.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.healthcare.R

class BuyMedicineActivity : AppCompatActivity() {

    private val packages = arrayOf(
        arrayOf("Uprise-D3 1000IU Capsule", "50"),
        arrayOf("HealthVit Chromium Picolinate 200mcg", "305"),
        arrayOf("Vitamin B Complex Capsules", "448"),
        arrayOf("Inlife Vitamin E Wheat Germ Oil Capsule", "539"),
        arrayOf("Dolo 650 Tablet", "30"),
        arrayOf("Crocin 650 Advance Tablet", "50"),
        arrayOf("Strepsils Medicated Lozenges for Sore Throat", "40"),
        arrayOf("Tata 1mg Calcium + Vitamin D3", "30"),
        arrayOf("Feronia -XT Tablet", "130")
    )
    
    private val package_details = arrayOf(
        "Building and keeping the bones & teeth strong\nReducing Fatigue/stress and muscular pains\nBoosting immunity and increasing resistance against infection",
        "Chromium is an essential trace mineral that plays an important role in helping insulin regulate blood glucose.",
        "Provides relief from vitamin B deficiencies\nHelps in formation of red blood cells\nMaintains healthy nervous system",
        "It promotes health as well as skin benefit.\nIt helps reduce skin blemish and pigmentation.\nIt acts as safeguard the skin from the harsh UVA and UVB sun rays.",
        "Dolo 650 Tablet helps relieve pain and fever by blocking the release of certain chemical messengers responsible for fever and pain.",
        "Crocin 650 Advance Tablet helps relieve pain and fever by blocking the release of certain chemical messengers responsible for fever and pain.",
        "Relieves the symptoms of a bacterial throat infection and soothes the recovery process\nProvides a warm and comforting feeling during sore throat",
        "Reduces the risk of calcium deficiency, Rickets, and Osteoporosis\nPromotes mobility and flexibility of joints",
        "Helps to reduce the iron deficiency due to chronic blood loss or low intake of iron"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_medicine)

        val listView: ListView = findViewById(R.id.listViewBM)
        
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
            val it = Intent(this, BuyMedicineDetailsActivity::class.java)
            it.putExtra("text1", packages[i][0])
            it.putExtra("text2", packages[i][1])
            it.putExtra("text3", package_details[i])
            startActivity(it)
        }
    }
}
