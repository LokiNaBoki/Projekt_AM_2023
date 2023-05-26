package com.example.projekt_am_2023.section

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.updateMargins
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekt_am_2023.R
import com.example.projekt_am_2023.database.DatabaseLoader
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.io.Serializable

class SectionListFragment: Fragment() {
    private var sections: MutableList<Section> = mutableListOf()
    private lateinit var listener: SectionListListener
    private lateinit var sectionAdapter: SectionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listener = if(parentFragment == null) {
            requireContext() as SectionListListener
        } else {
            parentFragment as SectionListListener
        }

        DatabaseLoader.sections.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                sections = Section.loadDatabaseArray(dataSnapshot)
                sectionAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("Firebase", "sections - loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_section_list, container, false)

        sectionAdapter = SectionAdapter()
        view.findViewById<RecyclerView>(R.id.sectionRecycler).apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = sectionAdapter
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = SectionListFragment().apply {
            arguments = Bundle().apply { }
        }
    }

    interface SectionListListener: Serializable {
        fun onSectionSelected(section: Section)
    }

    inner class SectionAdapter: RecyclerView.Adapter<SectionAdapter.ViewHolder>() {
        inner class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {
            val text: TextView

            init {
                text = view as TextView
                text.setOnClickListener(this)
            }

            override fun onClick(view: View?) {
                listener.onSectionSelected(sections[adapterPosition])
            }
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(TextView(viewGroup.context).apply {
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    updateMargins(bottom = (10 * resources.displayMetrics.density).toInt())
                }
                textSize = 22f
                setTextColor(Color.BLACK)
            })
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.text.text = sections[position].name
        }

        override fun getItemCount() = sections.size
    }
}