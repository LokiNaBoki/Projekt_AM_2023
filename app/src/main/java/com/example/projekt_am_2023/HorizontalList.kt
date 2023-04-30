package com.example.projekt_am_2023

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.marginEnd
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HorizontalList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vertical_list)

        val items: ArrayList<Int> = intent.getIntegerArrayListExtra("items") ?: arrayListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

        val recycler = findViewById<RecyclerView>(R.id.testRecycler)
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recycler.adapter = Adapter(items)
    }

    inner class Adapter(private var items: List<Int>) : RecyclerView.Adapter<Adapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView

            init {
                name = view.findViewById(R.id.itemText)
            }
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            val tv = TextView(this@HorizontalList)
            tv.id = R.id.itemText
            tv.setPadding(0, 0, 40, 0)
            return ViewHolder(tv)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.name.text = "Item: ${items[position]}"
        }

        override fun getItemCount() = items.size
    }
}