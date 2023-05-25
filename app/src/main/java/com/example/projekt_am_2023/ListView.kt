package com.example.projekt_am_2023

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"

class ListView : Fragment() {

    private var day: Int? = -1
    private var month: Int? = -1
    private var year: Int? = -1
    private lateinit var listViewRecycler : RecyclerView
    private lateinit var tasks: List<Section>
    private lateinit var trimmedTasks: List<Section>
    private lateinit var listViewAdapter: ListViewAdapter

    inner class SubtaskListener(private val task: Task) : View.OnClickListener {
        override fun onClick(view: View?) {
            Toast.makeText(context, "onClick ${task.title}", Toast.LENGTH_SHORT).show()
            //calendar.get(Calendar.DAY_OF_MONTH)
        }
    }

    inner class HideTasksListener(private val viewHolder: ListViewAdapter.ViewHolder, private val size: Int) : View.OnClickListener {
        override fun onClick(view: View?) {
            if (viewHolder.tasksRecycler.visibility == View.VISIBLE) {
                viewHolder.tasksRecycler.visibility = View.GONE
                viewHolder.tasksNum.text = "(${size} tasks)"
                viewHolder.hideButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.folded_triangle))
            }
            else {
                viewHolder.tasksRecycler.visibility = View.VISIBLE
                viewHolder.tasksNum.text = ""
                viewHolder.hideButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.unfolded_triangle))
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(activity, (arguments?.getInt(ARG_PARAM2)).toString(), Toast.LENGTH_SHORT).show()
        arguments?.let {
            day = it.getInt(ARG_PARAM1)
            month = it.getInt(ARG_PARAM2)
            year = it.getInt(ARG_PARAM3)
        }
        month = month!! - 1
        Log.i("data", "$day $month $year")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_list_view, container, false)

        listViewRecycler = view.findViewById(R.id.listViewRecycler)
        listViewRecycler.layoutManager = LinearLayoutManager(view.context)
        tasks = generateData()

        if (day == -1)
            trimmedTasks = tasks
        else {
            trimmedTasks = switchAdapterDate()
        }
        listViewAdapter = ListViewAdapter()
        listViewRecycler.adapter = listViewAdapter
        return view
    }

    fun changeDate(calendar: Calendar?) {
        day = calendar!!.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)
        year = calendar.get(Calendar.YEAR)
        trimmedTasks = switchAdapterDate()
        listViewAdapter.notifyDataSetChanged()

    }

    private fun switchAdapterDate(): List<Section> {
        var i = -1
        val trimmedTasks = ArrayList<Section>()
        for (section in tasks) {
            for (task in section.tasks) {
                if (
                    (task.endCalendar?.get(Calendar.DAY_OF_MONTH) == day) and
                    (month == task.endCalendar?.get(Calendar.MONTH)) and
                    (year == task.endCalendar?.get(Calendar.YEAR))
                ) {
                    if (trimmedTasks.isEmpty() || trimmedTasks.last().name != section.name) {
                        trimmedTasks.add(Section(section.name, ArrayList()))
                        i += 1
                    }
                    trimmedTasks[i].tasks.add(task)
                }
            }
        }
        return trimmedTasks
    }

    companion object {
        fun newInstance(calendar: Calendar?): ListView {
            val frag = ListView()
            frag.apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, calendar!!.get(Calendar.DAY_OF_MONTH))
                    putInt(ARG_PARAM2, calendar.get(Calendar.MONTH))
                    putInt(ARG_PARAM3, calendar.get(Calendar.YEAR))
                }
            }
            return frag
        }

        fun newInstance(): ListView {
            val frag = ListView()
            frag.apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, -1)
                    putInt(ARG_PARAM2, -1)
                    putInt(ARG_PARAM3, -1)
                }
            }
            return frag
        }
    }


    inner class ListViewAdapter() : RecyclerView.Adapter<ListViewAdapter.ViewHolder>() {

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
            return trimmedTasks.size
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.name.text = trimmedTasks[position].name
            viewHolder.tasksRecycler.removeAllViews()
            for (task in trimmedTasks[position].tasks) {
                viewHolder.tasksRecycler.removeAllViews()
            }
            for (task in trimmedTasks[position].tasks) {
                val tmp = LayoutInflater.from(context).inflate(R.layout.list_view_task, null, false)
                tmp.findViewById<CheckBox>(R.id.doneCheckBox).isChecked = task.done
                tmp.findViewById<TextView>(R.id.taskTitle).text = task.title
                tmp.findViewById<TextView>(R.id.taskDate).text = task.getEndDate()
                val drawableId: Int = task.assignee?.avatar ?: R.drawable.user_default
                val drawable = ContextCompat.getDrawable(requireContext(), drawableId)
                tmp.findViewById<ImageView>(R.id.userImage).setImageDrawable(drawable)
                val listener = SubtaskListener(task)
                tmp.setOnClickListener(listener)
                viewHolder.tasksRecycler.addView(tmp)
            }
            val listener = HideTasksListener(viewHolder, trimmedTasks[position].tasks.size)
            viewHolder.hideButton.setOnClickListener(listener)
        }
    }

    private fun generateData() : List<Section> {
        val user1 = User("User 1", R.drawable.user1)
        val user2 = User("User 2", R.drawable.user2)

        val tag1 = Tag("First", Color.RED)
        val tag2 = Tag("First", Color.BLUE)

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        return mutableListOf(
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
                        "Task 3.1", false, user1,
                        Calendar.getInstance().apply { time = sdf.parse("2023-03-03 05:05")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-05-05 03:03")!! },
                        "Description of task 3.1", mutableListOf(), mutableListOf(tag2)
                    ),
                    Task(
                        "Task 3.2", false, user2,
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-30 11:00")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-30 15:15")!! },
                        "Description of task 3.2", mutableListOf(), mutableListOf(tag2)
                    ),
                    Task(
                        "Task 3.3", false, user2,
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-28 15:00")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-29 20:11")!! },
                        "Description of task 3.2", mutableListOf(), mutableListOf(tag1)
                    )
                )
            ),
            Section(
                "Section 4",
                mutableListOf(
                    Task(
                        "Task 4.1", false, user2,
                        Calendar.getInstance().apply { time = sdf.parse("2023-03-28 17:05")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-05-28 19:03")!! },
                        "Description of task 4.1", mutableListOf(), mutableListOf()
                    ),
                    Task(
                        "Task 4.2", false, user2,
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-29 12:00")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-30 13:00")!! },
                        null, mutableListOf(), mutableListOf(tag2)
                    ),
                )
            ),
            Section(
                "Section 5",
                mutableListOf(
                    Task(
                        "Task 5.1", false, null,
                        Calendar.getInstance().apply { time = sdf.parse("2023-06-06 06:06")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-06-07 06:07")!! },
                        "Description of task 5.1", mutableListOf(), mutableListOf(tag2)
                    ),
                    Task(
                        "Task 5.2", false, user2,
                        Calendar.getInstance().apply { time = sdf.parse("2023-05-07 12:12")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-07-05 13:13")!! },
                        "Description of task 5.2", mutableListOf(), mutableListOf(tag1)
                    ),
                    Task(
                        "Task 5.3", false, user2,
                        Calendar.getInstance().apply { time = sdf.parse("2023-09-09 09:09")!! },
                        null,
                        "Description of task 5.3", mutableListOf(), mutableListOf(tag1)
                    ),
                    Task(
                        "Task 5.4", false, user1,
                        null,
                        Calendar.getInstance().apply { time = sdf.parse("2023-06-06 20:00")!! },
                        "Description of task 5.4", mutableListOf(), mutableListOf(tag2)
                    ),
                )
            ),
        )
    }
}