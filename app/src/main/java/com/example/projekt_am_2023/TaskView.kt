package com.example.projekt_am_2023

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

private const val ARG_TASK = "task"
private const val ARG_USERS = "users"

class TaskView : Fragment(), UserListFragment.AssigneeDialogListener {
    private var users: ArrayList<User> = arrayListOf()
    private lateinit var task: Task
    private lateinit var subtasksLayout: LinearLayout

    inner class SubtaskListener(private  val subtask: Task) : View.OnClickListener, View.OnLongClickListener {
        override fun onClick(view: View?) {
            val i = Intent(context, EditTask::class.java)
            i.putExtra("task", subtask)
            i.putExtra("users", users)
            startActivity(i)
        }

        override fun onLongClick(view: View?): Boolean {
            val index = task.subtasks.indexOf(subtask)
            subtasksLayout.removeViewAt(index)
            task.subtasks.removeAt(index)
            return true
        }
    }

    inner class NewSubtaskListener : View.OnClickListener {
        override fun onClick(view: View?) {
            val i = Intent(context, AddTask::class.java)
            i.putExtra("users", users)
            startActivity(i)
        }
    }

    private fun onTimeClick(time: Calendar, callback: () -> Unit) {
        val timePickerDialog = TimePickerDialog(requireContext(),
            { _: TimePicker, hour: Int, minute: Int ->
                time.set(Calendar.HOUR_OF_DAY, hour)
                time.set(Calendar.MINUTE, minute)
                callback()
            }, time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), true
        )
        timePickerDialog.show()
    }

    private fun onDateClick(date: Calendar, callback: () -> Unit) {
        val datePickerDialog = DatePickerDialog(requireContext(),
            { _: DatePicker, year: Int, month: Int, day: Int ->
                date.set(Calendar.YEAR, year)
                date.set(Calendar.MONTH, month)
                date.set(Calendar.DAY_OF_MONTH, day)
                callback()
            }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        task = if (arguments != null) {
            users = arguments?.getSerializable(ARG_USERS) as ArrayList<User>
            requireArguments().getSerializable(ARG_TASK) as Task
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

        view.findViewById<CheckBox>(R.id.doneCheckbox).apply {
            isChecked = task.done
            setOnCheckedChangeListener { _, isChecked -> task.done = isChecked }
        }

        view.findViewById<TextView>(R.id.taskTitle).apply {
            text = task.title
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

                override fun afterTextChanged(s: Editable?) {
                    task.title = s.toString()
                }
            })
        }

        view.findViewById<AssigneeComponent>(R.id.assignee).setOnClickListener {
            UserListFragment.newInstance(ArrayList(users)).show(childFragmentManager, null)
        }

        view.findViewById<TextView>(R.id.startDate).apply{
            text = task.getStartDate()
            setOnClickListener {
                if(task.startCalendar == null) { task.startCalendar = Calendar.getInstance() }
                onDateClick(task.startCalendar!!) {
                    (it as TextView).text = task.getStartDate()
                }
            }
        }
        view.findViewById<TextView>(R.id.startTime).apply{
            text = task.getStartTime()
            setOnClickListener {
                if(task.startCalendar == null) { task.startCalendar = Calendar.getInstance() }
                onTimeClick(task.startCalendar!!) {
                    (it as TextView).text = task.getStartTime()
                }
            }
        }

        view.findViewById<TextView>(R.id.endDate).apply{
            text = task.getEndDate()
            setOnClickListener {
                if(task.endCalendar == null) { task.endCalendar = Calendar.getInstance() }
                onDateClick(task.endCalendar!!) {
                    (it as TextView).text = task.getEndDate()
                }
            }
        }
        view.findViewById<TextView>(R.id.endTime).apply{
            text = task.getEndTime()
            setOnClickListener {
                if(task.endCalendar == null) { task.endCalendar = Calendar.getInstance() }
                onTimeClick(task.endCalendar!!) {
                    (it as TextView).text = task.getEndTime()
                }
            }
        }

        view.findViewById<EditText>(R.id.descriptionText).apply{
            setText(task.Description)
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

                override fun afterTextChanged(s: Editable?) {
                    task.Description = s.toString()
                }
            })
        }

        subtasksLayout = view.findViewById(R.id.subtasksRecycler)
        for(subtask in task.subtasks) {
            val listener = SubtaskListener(subtask)

            subtasksLayout.addView(LayoutInflater.from(context).inflate(R.layout.subtask, subtasksLayout, false).apply {
                findViewById<CheckBox>(R.id.doneCheckbox).apply {
                    isChecked = subtask.done
                    setOnCheckedChangeListener { _, isChecked -> subtask.done = isChecked  }
                }
                findViewById<TextView>(R.id.subtaskTitle).text = subtask.title
                setOnClickListener(listener)
                setOnLongClickListener(listener)
            })
        }

        subtasksLayout.addView(LayoutInflater.from(context).inflate(R.layout.new_element, subtasksLayout, false).apply {
            layoutParams = layoutParams.apply { width = ViewGroup.LayoutParams.MATCH_PARENT }
            findViewById<TextView>(R.id.newText).text = getString(R.string.addSubtask)
            setOnClickListener(NewSubtaskListener())
        })

        view.findViewById<RecyclerView>(R.id.tagsRecycler).apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = TagAdapter(task.tags)
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(task: Task, users: ArrayList<User>) = TaskView().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_TASK, task)
                    putSerializable(ARG_USERS, users)
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

            inner class TagClick : View.OnClickListener, View.OnLongClickListener {
                override fun onClick(view: View?) {
                    Toast.makeText(context, "onClick ${tags[adapterPosition].name}", Toast.LENGTH_SHORT).show()
                }

                override fun onLongClick(view: View?): Boolean {
                    Toast.makeText(context, "onLongClick ${tags[adapterPosition].name}", Toast.LENGTH_SHORT).show()
                    return false
                }
            }

            inner class NewClick : View.OnClickListener {
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
                    viewHolder.name.apply {
                        text = getString(R.string.addTag)
                    }
                }
                REGULAR -> {
                    viewHolder.name.apply{
                        text = tags[position].name
                        backgroundTintList = ColorStateList.valueOf(tags[position].color)
                    }

                }
            }
        }

        override fun getItemCount() = tags.size + 1
    }

    fun getTask() = task

    override fun onFinishAssigneeDialog(assignee: User) {
        requireView().findViewById<AssigneeComponent>(R.id.assignee).user = assignee
        task.assignee = assignee
    }
}