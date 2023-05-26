package com.example.projekt_am_2023

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekt_am_2023.database.DatabaseLoader
import com.example.projekt_am_2023.section.Section
import com.example.projekt_am_2023.task.EditTask
import com.example.projekt_am_2023.task.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

private const val ARG_DAY = "day"
private const val ARG_MONTH = "month"
private const val ARG_YEAR = "year"

class ListView: Fragment() {
    private var day: Int? = -1
    private var month: Int? = -1
    private var year: Int? = -1
    private lateinit var listViewRecycler: RecyclerView
    private lateinit var tasks: List<Section>
    private lateinit var trimmedTasks: List<Section>
    private lateinit var listViewAdapter: ListViewAdapter

    inner class SubtaskListener(private val task: Task): View.OnClickListener {
        override fun onClick(view: View?) {
            val i = Intent(context, EditTask::class.java)
            i.putExtra("task", task)
            startActivity(i)
        }
    }

    inner class HideTasksListener(
        private val viewHolder: ListViewAdapter.ViewHolder,
        private val size: Int
    ): View.OnClickListener {
        override fun onClick(view: View?) {
            if(viewHolder.tasksRecycler.visibility == View.VISIBLE) {
                viewHolder.tasksRecycler.visibility = View.GONE
                viewHolder.tasksNum.text = resources.getQuantityString(
                    R.plurals.section_task_count, size, size
                )
                viewHolder.hideButton.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.folded_triangle)
                )
            } else {
                viewHolder.tasksRecycler.visibility = View.VISIBLE
                viewHolder.tasksNum.text = ""
                viewHolder.hideButton.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.unfolded_triangle)
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            day = it.getInt(ARG_DAY)
            month = it.getInt(ARG_MONTH)
            year = it.getInt(ARG_YEAR)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_list_view, container, false)

        listViewRecycler = view.findViewById(R.id.listViewRecycler)
        listViewRecycler.layoutManager = LinearLayoutManager(view.context)
        tasks = ArrayList()
        trimmedTasks = ArrayList()
        listViewAdapter = ListViewAdapter()
        listViewRecycler.adapter = listViewAdapter

        val postListener = object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                tasks = DatabaseLoader.loadDatabase(dataSnapshot)
                trimmedTasks = switchAdapterDate()
                listViewAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("Firebase", "loadPost:onCancelled", databaseError.toException())
            }
        }
        DatabaseLoader.dataref.addValueEventListener(postListener)

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
        if(year == -1) {
            return tasks
        } else {
            var i = -1
            val temp = ArrayList<Section>()
            for(section in tasks) {
                for(task in section.tasks) {
                    if((task.endCalendar?.get(Calendar.DAY_OF_MONTH) == day)
                       and (month == task.endCalendar?.get(Calendar.MONTH))
                       and (year == task.endCalendar?.get(Calendar.YEAR))
                    ) {
                        if(temp.isEmpty() || temp.last().name != section.name) {
                            temp.add(Section(section.name, ArrayList()))
                            i += 1
                        }
                        temp[i].tasks.add(task)
                    }
                }
            }
        return temp
        }
    }

    companion object {
        fun newInstance(calendar: Calendar?): ListView {
            val frag = ListView()
            frag.apply {
                arguments = Bundle().apply {
                    putInt(ARG_DAY, calendar!!.get(Calendar.DAY_OF_MONTH))
                    putInt(ARG_MONTH, calendar.get(Calendar.MONTH))
                    putInt(ARG_YEAR, calendar.get(Calendar.YEAR))
                }
            }
            return frag
        }

        fun newInstance(): ListView {
            val frag = ListView()
            frag.apply {
                arguments = Bundle().apply {
                    putInt(ARG_DAY, -1)
                    putInt(ARG_MONTH, -1)
                    putInt(ARG_YEAR, -1)
                }
            }
            return frag
        }
    }

    inner class ListViewAdapter: RecyclerView.Adapter<ListViewAdapter.ViewHolder>() {
        inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.sectionTitle)
            val tasksRecycler: LinearLayout = view.findViewById(R.id.tasksRecycler)
            val tasksNum: TextView = view.findViewById(R.id.tasksNum)
            val hideButton: ImageButton = view.findViewById(R.id.hideTasks)
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(viewGroup.context).inflate(
                    R.layout.list_view_section, viewGroup, false
                )
            )
        }

        override fun getItemCount() = trimmedTasks.size

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.name.text = trimmedTasks[position].name
            viewHolder.tasksRecycler.removeAllViews()

            for(task in trimmedTasks[position].tasks) {
                viewHolder.tasksRecycler.removeAllViews()
            }

            for(task in trimmedTasks[position].tasks) {
                val tmp = LayoutInflater.from(context).inflate(
                    R.layout.list_view_task, viewHolder.tasksRecycler, false
                )
                tmp.findViewById<CheckBox>(R.id.doneCheckBox).isChecked = task.done
                tmp.findViewById<CheckBox>(R.id.doneCheckBox).setOnCheckedChangeListener { _, b ->
                    task.done = b
                    task.saveDatabase()
                }
                tmp.findViewById<TextView>(R.id.taskTitle).text = task.title
                tmp.findViewById<TextView>(R.id.taskDate).text = task.getEndDate()
                task.assignee?.loadAvatar(requireContext(), tmp.findViewById(R.id.userImage))
                val listener = SubtaskListener(task)
                tmp.setOnClickListener(listener)
                viewHolder.tasksRecycler.addView(tmp)
            }
            val listener = HideTasksListener(viewHolder, trimmedTasks[position].tasks.size)
            viewHolder.hideButton.setOnClickListener(listener)
        }
    }
}