package com.example.projekt_am_2023

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TaskView : AppCompatActivity() {
    inner class NewSubtaskListener() : View.OnClickListener {
        override fun onClick(view: View?) {
            Toast.makeText(this@TaskView, "onClick New Subtask", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_view)

        val tagRecycler = findViewById<RecyclerView>(R.id.tagsRecycler)
        tagRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        tagRecycler.adapter = TagAdapter(emptyList())

        val subtasks = findViewById<LinearLayout>(R.id.subtasksRecycler)
        val tmp = LayoutInflater.from(this).inflate(R.layout.new_element, null, false)
        tmp.findViewById<TextView>(R.id.newText).text = getString(R.string.addSubtask)
        tmp.setOnClickListener(NewSubtaskListener())
        subtasks.addView(tmp)
    }

    inner class TagAdapter(private var tags: List<Tag>) : RecyclerView.Adapter<TagAdapter.ViewHolder>() {
        private val REGULAR = 0
        private val ADDTAG = 1

        override fun getItemViewType(position: Int): Int {
            return if (position == tags.size) { ADDTAG } else { REGULAR }
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView

            init {
                name = view.findViewById(R.id.tagName) ?: view.findViewById(R.id.newText)

                view.findViewById<ConstraintLayout>(R.id.newElementLayout)?.apply {
                    setOnClickListener(NewClick())
                }

                view.findViewById<ConstraintLayout>(R.id.tagLayout)?.apply {
                    val listener = TagClick()
                    setOnClickListener(listener)
                    setOnLongClickListener(listener)
                }
            }

            inner class TagClick() : View.OnClickListener, View.OnLongClickListener {
                override fun onClick(view: View?) {
                    Toast.makeText(this@TaskView, "onClick ${tags[adapterPosition].name}", Toast.LENGTH_SHORT).show()
                }

                override fun onLongClick(view: View?): Boolean {
                    Toast.makeText(this@TaskView, "onLongClick ${tags[adapterPosition].name}", Toast.LENGTH_SHORT).show()
                    return false
                }
            }

            inner class NewClick() : View.OnClickListener {
                override fun onClick(view: View?) {
                    Toast.makeText(this@TaskView, "onClick New", Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            return when(viewType) {
                ADDTAG -> {
                    ViewHolder(LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.new_element, viewGroup, false))
                }
                REGULAR -> {
                    ViewHolder(LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.tag, viewGroup, false))
                }
                else -> throw IllegalArgumentException("Invalid view type")
            }
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            when (viewHolder.itemViewType) {
                ADDTAG -> {
                    viewHolder.name!!.apply {
                        text = getString(R.string.addTag)
                    }
                }
                REGULAR -> {
                    viewHolder.name!!.apply{
                        text = tags[position].name
                        backgroundTintList = ColorStateList.valueOf(tags[position].color)
                    }

                }
            }
        }

        override fun getItemCount() = tags.size + 1
    }
}