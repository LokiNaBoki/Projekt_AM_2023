package com.example.projekt_am_2023

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val ARG_PARAM1 = "task"
class TaskView : Fragment() {
    private lateinit var task: Task

    inner class SubtaskListener(private  val task: Task) : View.OnClickListener, View.OnLongClickListener {
        override fun onClick(view: View?) {
            val i = Intent(context, EditTask::class.java)
            i.putExtra("task", task)
            startActivity(i)
        }

        override fun onLongClick(view: View?): Boolean {
            Toast.makeText(context, "onLongClick ${task.title}", Toast.LENGTH_SHORT).show()
            return true
        }
    }

    inner class NewSubtaskListener() : View.OnClickListener {
        override fun onClick(view: View?) {
            val i = Intent(context, EditTask::class.java)
            i.putExtra("task", Task.emptyTask())
            startActivity(i)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        task = if (arguments != null) {
            requireArguments().getSerializable(ARG_PARAM1) as Task
        } else {
            Task.emptyTask()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_view, container, false)
        val tagRecycler = view.findViewById<RecyclerView>(R.id.tagsRecycler)
        tagRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        view.findViewById<CheckBox>(R.id.doneCheckbox).isChecked = task.done
        view.findViewById<TextView>(R.id.taskTitle).text = task.title

        val drawableId: Int = task.assignee?.avatar ?: R.drawable.user_default
        val drawable = ContextCompat.getDrawable(requireContext(), drawableId)
        view.findViewById<ImageView>(R.id.userImage).setImageDrawable(drawable)
        view.findViewById<TextView>(R.id.userText).text = task.assignee?.name ?: ""

        view.findViewById<TextView>(R.id.startDate).text = task.getStartDate()
        view.findViewById<TextView>(R.id.startTime).text = task.getStartTime()

        view.findViewById<TextView>(R.id.endDate).text = task.getEndDate()
        view.findViewById<TextView>(R.id.endTime).text = task.getEndTime()

        view.findViewById<EditText>(R.id.descriptionText).setText(task.Description)

        tagRecycler.adapter = TagAdapter(task.tags)

        val subtasks = view.findViewById<LinearLayout>(R.id.subtasksRecycler)
        for(subtask in task.subtasks) {
            val tmp = LayoutInflater.from(context).inflate(R.layout.subtask, null, false)
            tmp.findViewById<CheckBox>(R.id.doneCheckbox).isChecked = subtask.done
            tmp.findViewById<TextView>(R.id.subtaskTitle).text = subtask.title

            val listener = SubtaskListener(subtask)
            tmp.setOnClickListener(listener)
            tmp.setOnLongClickListener(listener)
            subtasks.addView(tmp)
        }

        val tmp = LayoutInflater.from(context).inflate(R.layout.new_element, null, false)
        tmp.findViewById<TextView>(R.id.newText).text = getString(R.string.addSubtask)
        tmp.setOnClickListener(NewSubtaskListener())
        subtasks.addView(tmp)

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(task: Task) = TaskView().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, task)
                }
            }
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
                    Toast.makeText(context, "onClick ${tags[adapterPosition].name}", Toast.LENGTH_SHORT).show()
                }

                override fun onLongClick(view: View?): Boolean {
                    Toast.makeText(context, "onLongClick ${tags[adapterPosition].name}", Toast.LENGTH_SHORT).show()
                    return false
                }
            }

            inner class NewClick() : View.OnClickListener {
                override fun onClick(view: View?) {
                    Toast.makeText(context, "onClick New", Toast.LENGTH_SHORT).show()
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

    fun getTask() = task
}