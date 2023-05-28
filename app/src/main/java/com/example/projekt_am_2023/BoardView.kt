package com.example.projekt_am_2023

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekt_am_2023.database.DatabaseLoader
import com.example.projekt_am_2023.section.Section
import com.example.projekt_am_2023.tag.Tag
import com.example.projekt_am_2023.tag.TagComponent
import com.example.projekt_am_2023.task.EditTask
import com.example.projekt_am_2023.task.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import org.w3c.dom.Text


class BoardView : Fragment() {

    private lateinit var boardViewRecycler: RecyclerView
    private lateinit var boardViewAdapter: BoardViewAdapter
    private lateinit var tasks: List<Section>

    inner class SubtaskListener(private val task: Task) : View.OnClickListener {
        override fun onClick(view: View?) {
            val i = Intent(context, EditTask::class.java)
            i.putExtra("task", task)
            startActivity(i)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_board_view, container, false)
        boardViewRecycler = view.findViewById(R.id.boardViewRecycler)
        boardViewRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        tasks = ArrayList()
        boardViewAdapter = BoardViewAdapter()
        boardViewRecycler.adapter = boardViewAdapter

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                tasks = DatabaseLoader.loadDatabase(dataSnapshot)
                boardViewAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("Firebase", "loadPost:onCancelled", databaseError.toException())
            }
        }
        DatabaseLoader.dataref.addValueEventListener(postListener)
        return view
    }

    inner class BoardViewAdapter : RecyclerView.Adapter<BoardViewAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.sectionName)
            val sectionRecycler: RecyclerView = view.findViewById(R.id.sectionRecycler)
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int ): ViewHolder {
            return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.board_view_section, viewGroup, false))
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.name.text = tasks[position].name
            viewHolder.sectionRecycler.layoutManager = LinearLayoutManager(context)
            val sectionAdapter = SectionAdapter(tasks[position])
            viewHolder.sectionRecycler.adapter = sectionAdapter
        }

        override fun getItemCount(): Int {
            return tasks.size
        }
    }

    inner class SectionAdapter(sectionStart: Section) : RecyclerView.Adapter<SectionAdapter.ViewHolder>() {
        var section: Section

        init {
            section = sectionStart
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val checkBox : CheckBox = view.findViewById(R.id.doneCheckBox)
            val taskName: TextView = view.findViewById(R.id.taskName)
            val userImage: ImageView = view.findViewById(R.id.userImage)
            val date: TextView = view.findViewById(R.id.dateText)
            val tagLayout: LinearLayout = view.findViewById(R.id.boardTagLayout)
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): SectionAdapter.ViewHolder {
            return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.board_view_task, viewGroup, false))
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.checkBox.isChecked = section.tasks[position].done
            viewHolder.checkBox.setOnCheckedChangeListener { _, b ->
                section.tasks[position].done = b
                section.tasks[position].saveDatabase()
            }
            viewHolder.taskName.text = section.tasks[position].title
            section.tasks[position].assignee?.loadAvatar(requireContext(), viewHolder.userImage)
            viewHolder.date.text = section.tasks[position].getEndDate()
            for (tag in section.tasks[position].tags) {
                viewHolder.tagLayout.removeAllViews()
            }
            val tmp = LayoutInflater.from(context).inflate(R.layout.board_view_tag, null, false)
            val tag: Tag = Tag()
            tag.name = ""
            tag.color = 1
            tmp.findViewById<TagComponent>(R.id.tag).setTag(tag)
            tmp.findViewById<TagComponent>(R.id.tag).name
            val listener = SubtaskListener(section.tasks[position])
            viewHolder.itemView.setOnClickListener(listener)
            viewHolder.tagLayout.addView(tmp)

            for (tag in section.tasks[position].tags) {
                val tmp = LayoutInflater.from(context).inflate(R.layout.board_view_tag, null, false)
                tmp.findViewById<TagComponent>(R.id.tag).setTag(tag)
                tmp.findViewById<TagComponent>(R.id.tag).name
                val listener = SubtaskListener(section.tasks[position])
                viewHolder.itemView.setOnClickListener(listener)
                viewHolder.tagLayout.addView(tmp)
            }
        }

        override fun getItemCount(): Int {
            return section.tasks.size
        }
    }

    companion object {
        fun newInstance(): BoardView {
            return BoardView()
        }
    }
}