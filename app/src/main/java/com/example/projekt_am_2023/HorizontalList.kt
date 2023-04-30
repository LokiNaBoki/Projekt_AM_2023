package com.example.projekt_am_2023

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val ARG_PARAM1 = "items"

class HorizontalList : Fragment() {
    private lateinit var items: ArrayList<Int>
    private lateinit var adapter: Adapter

    public fun notifyAdded(pos: Int) {
        adapter?.notifyItemInserted(pos)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        items = if(arguments != null) {
            requireArguments().getIntegerArrayList(ARG_PARAM1) ?: arrayListOf()
        } else {
            arrayListOf()
        }

        adapter = Adapter(items)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_horizontal_list, container, false)

        val recycler = view.findViewById<RecyclerView>(R.id.testRecycler)
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recycler.adapter = adapter

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: ArrayList<Int>) =
            HorizontalList().apply {
                arguments = Bundle().apply {
                    putIntegerArrayList(ARG_PARAM1, param1)
                }
            }
    }

    inner class Adapter(private var items: List<Int>) : RecyclerView.Adapter<Adapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView

            init {
                name = view.findViewById(R.id.itemText)
            }
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            val tv = TextView(context)
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