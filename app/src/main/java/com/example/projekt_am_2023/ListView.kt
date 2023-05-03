package com.example.projekt_am_2023

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ListView : AppCompatActivity() {

    inner class SubtaskListener(private val task: Task) : View.OnClickListener {
        override fun onClick(view: View?) {
            Toast.makeText(this@ListView, "onClick ${task.title}", Toast.LENGTH_SHORT).show()
        }
    }

    inner class HideTasksListener(private val viewHolder: ListViewAdapter.ViewHolder, private val size: Int) : View.OnClickListener {
        override fun onClick(view: View?) {
            if (viewHolder.tasksRecycler.visibility == View.VISIBLE) {
                viewHolder.tasksRecycler.visibility = View.GONE
                viewHolder.tasksNum.text = "(${size} tasks)"
                viewHolder.hideButton.setImageDrawable(ContextCompat.getDrawable(this@ListView, R.drawable.folded_triangle))
            }
            else {
                viewHolder.tasksRecycler.visibility = View.VISIBLE
                viewHolder.tasksNum.text = ""
                viewHolder.hideButton.setImageDrawable(ContextCompat.getDrawable(this@ListView, R.drawable.unfolded_triangle))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_view)

        val listViewRecycler = findViewById<RecyclerView>(R.id.listViewRecycler)
        listViewRecycler.layoutManager = LinearLayoutManager(this)
        val tasks = generateData()
        listViewRecycler.adapter = ListViewAdapter(tasks)

    }

    inner class ListViewAdapter(private var sections: List<Section>) : RecyclerView.Adapter<ListViewAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.sectionTitle)
            val tasksRecycler: LinearLayout = view.findViewById(R.id.tasksRecycler)
            val tasksNum: TextView = view.findViewById(R.id.tasksNum)
            val hideButton: ImageButton = view.findViewById(R.id.hideTasks)
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.list_view_section, viewGroup, false))
        }

        override fun getItemCount(): Int {
            return sections.size
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.name.text = sections[position].name
            for (task in sections[position].tasks) {
                val tmp = LayoutInflater.from(this@ListView).inflate(R.layout.list_view_task, null, false)
                tmp.findViewById<CheckBox>(R.id.doneCheckBox).isChecked = task.done
                tmp.findViewById<TextView>(R.id.taskTitle).text = task.title
                tmp.findViewById<TextView>(R.id.taskDate).text = task.getEndDate()
                val drawableId: Int = task.assignee?.avatar ?: R.drawable.user_default
                val drawable = ContextCompat.getDrawable(this@ListView, drawableId)
                tmp.findViewById<ImageView>(R.id.userImage).setImageDrawable(drawable)
                val listener = SubtaskListener(task)
                tmp.setOnClickListener(listener)
                viewHolder.tasksRecycler.addView(tmp)
            }
            val listener = HideTasksListener(viewHolder, sections[position].tasks.size)
            viewHolder.hideButton.setOnClickListener(listener)
        }
    }

    private fun generateData() : List<Section> {
        val user1 = User("User 1", R.drawable.user1)
        val user2 = User("User 2", R.drawable.user2)

        val tag1 = Tag("First", Color.RED)
        val tag2 = Tag("First", Color.BLUE)

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:MM", Locale.getDefault())

        val tasks = mutableListOf(
            Section(
                "Section 1",
                mutableListOf(
                    Task(
                        "Task 1.1", false, user1,
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-27 10:10")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-28 11:11")!! },
                        "Description of task 1.1", mutableListOf(), mutableListOf(tag1)
                    ),
                    Task(
                        "Task 1.2", false, user1,
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-26 09:05")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-29 12:20")!! },
                        "Description of task 1.2", mutableListOf(), mutableListOf(tag1, tag2)
                    )
                )
            ),
            Section(
                "Section 2",
                mutableListOf(
                    Task(
                        "Task 2.1", false, user1,
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-29 02:09")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-29 02:19")!! },
                        "Description of task 1.1", mutableListOf(), mutableListOf(tag1)
                    ),
                    Task(
                        "Task 2.2", false, user2,
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-26 09:05")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-29 12:20")!! },
                        "Description of task 2.2", mutableListOf(), mutableListOf(tag1, tag2)
                    ),
                    Task(
                        "Task 2.3", true, user2,
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-27 10:05")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-30 11:20")!! },
                        "Description of task 2.2", mutableListOf(), mutableListOf(tag1, tag2)
                    )
                )
            ),
            Section(
                "Section 3",
                mutableListOf(
                    Task(
                        "Task 3.1", false, null,
                        Calendar.getInstance().apply { time = sdf.parse("2023-06-06 06:06")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-06-07 06:07")!! },
                        "Description of task 3.1", mutableListOf(), mutableListOf(tag2)
                    ),
                    Task(
                        "Task 3.2", false, user2,
                        Calendar.getInstance().apply { time = sdf.parse("2023-05-07 12:12")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-07-05 13:13")!! },
                        "Description of task 3.2", mutableListOf(), mutableListOf(tag1)
                    ),
                    Task(
                        "Task 3.3", false, user2,
                        Calendar.getInstance().apply { time = sdf.parse("2023-09-09 09:09")!! },
                        null,
                        "Description of task 3.3", mutableListOf(), mutableListOf(tag1)
                    ),
                    Task(
                        "Task 3.4", false, user1,
                        null,
                        Calendar.getInstance().apply { time = sdf.parse("2023-06-06 20:00")!! },
                        "Description of task 3.4", mutableListOf(), mutableListOf(tag2)
                    ),
                )
            ),
        )
        return tasks
    }
}