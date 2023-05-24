package com.example.projekt_am_2023

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

private const val ARG_TASK = "task"

class TaskView : Fragment(), UserListFragment.AssigneeDialogListener,
                 TagListFragment.TagDialogListener, SectionListFragment.SectionDialogListener {
    private lateinit var task: Task
    private lateinit var subtasksAdapter: SubtaskAdapter
    private lateinit var tagsAdapter: TagAdapter

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

        view.findViewById<AssigneeComponent>(R.id.assignee).apply {
            user = task.assignee
            setOnClickListener {
                UserListFragment.newInstance().show(childFragmentManager, null)
            }

            setOnLongClickListener {
                task.assignee = null
                user = task.assignee
                true
            }
        }

        view.findViewById<TextView>(R.id.sectionText).apply {
            text = task.section?.name ?: ""
            setOnClickListener {
                SectionListFragment.newInstance().show(childFragmentManager, null)
            }
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

        subtasksAdapter = SubtaskAdapter()
        view.findViewById<RecyclerView>(R.id.subtasksRecycler).apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = subtasksAdapter
        }

        tagsAdapter = TagAdapter()
        view.findViewById<RecyclerView>(R.id.tagsRecycler).apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = tagsAdapter
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(task: Task) = TaskView().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_TASK, task)
                }
            }
    }

    inner class TagAdapter() : RecyclerView.Adapter<TagAdapter.ViewHolder>() {
        private val REGULAR = 0
        private val ADDTAG = 1

        override fun getItemViewType(position: Int): Int {
            return if (position == task.tags.size) { ADDTAG } else { REGULAR }
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val text: TextView?

            init {
                text = view.findViewById(R.id.newText)

                view.findViewById<ConstraintLayout>(R.id.newElementLayout)?.apply {
                    setOnClickListener(NewClick())
                }

                if(text == null) {
                    view.apply {
                        setOnClickListener(TagClick())
                    }
                }
            }

            inner class TagClick : View.OnClickListener {
                override fun onClick(view: View?) {
                    task.tags.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                }
            }

            inner class NewClick : View.OnClickListener {
                override fun onClick(view: View?) {
                    TagListFragment.newInstance().show(childFragmentManager, null)
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
                    ViewHolder(TagComponent(viewGroup.context))
                }
                else -> throw IllegalArgumentException("Invalid view type")
            }
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            when (viewHolder.itemViewType) {
                ADDTAG -> {
                    viewHolder.text!!.apply {
                        text = getString(R.string.addTag)
                    }
                }
                REGULAR -> {
                    (viewHolder.itemView as TagComponent).apply{
                        layoutParams = (layoutParams as ViewGroup.MarginLayoutParams).apply {
                            updateMargins(right = (10 * resources.displayMetrics.density).toInt())
                            updateLayoutParams<ViewGroup.LayoutParams> {
                                height = ViewGroup.LayoutParams.MATCH_PARENT
                            }
                        }
                        setTag(task.tags[position])
                    }
                }
            }
        }

        override fun getItemCount() = task.tags.size + 1
    }

    private var editResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
            val newTask = (result.data!!.getSerializableExtra("task") as Task)
            val index = result.data!!.getIntExtra("index", 0)
            task.subtasks[index] = newTask
            subtasksAdapter.notifyItemChanged(index)
        } else {
            Toast.makeText(context, "Canceled", Toast.LENGTH_SHORT).show()
        }
    }

    private var addResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show()
            val newTask = (result.data!!.getSerializableExtra("task") as Task)
            task.subtasks.add(newTask)
            subtasksAdapter.notifyItemInserted(task.subtasks.size)
        } else {
            Toast.makeText(context, "Canceled", Toast.LENGTH_SHORT).show()
        }
    }

    inner class SubtaskAdapter() : RecyclerView.Adapter<SubtaskAdapter.ViewHolder>() {
        private val REGULAR = 0
        private val ADDSUBTASK = 1

        override fun getItemViewType(position: Int): Int {
            return if (position == task.subtasks.size) { ADDSUBTASK } else { REGULAR }
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView
            val checkbox: CheckBox?

            init {
                name = view.findViewById(R.id.subtaskTitle) ?: view.findViewById(R.id.newText)
                checkbox = view.findViewById(R.id.doneCheckbox)

                view.findViewById<ConstraintLayout>(R.id.newElementLayout)?.apply {
                    setOnClickListener(NewClick())
                }

                view.findViewById<ConstraintLayout>(R.id.subtaskLayoutOuter)?.apply {
                    val listener = SubtaskClick()
                    setOnClickListener(listener)
                    setOnLongClickListener(listener)
                }
            }

            inner class SubtaskClick : View.OnClickListener, View.OnLongClickListener {
                override fun onClick(view: View?) {
                    val i = Intent(context, EditTask::class.java)
                    i.putExtra("task", task.subtasks[adapterPosition])
                    i.putExtra("index", adapterPosition)
                    editResult.launch(i)
                }

                override fun onLongClick(p0: View?): Boolean {
                    task.subtasks.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                    return true
                }
            }

            inner class NewClick : View.OnClickListener {
                override fun onClick(view: View?) {
                    val i = Intent(context, AddTask::class.java)
                    addResult.launch(i)
                }
            }
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            return when(viewType) {
                ADDSUBTASK -> {
                    ViewHolder(LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.new_element, viewGroup, false).apply {
                            layoutParams = layoutParams.apply { width = ViewGroup.LayoutParams.MATCH_PARENT }
                    })
                }
                REGULAR -> {
                    ViewHolder(LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.subtask, viewGroup, false))
                }
                else -> throw IllegalArgumentException("Invalid view type")
            }
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            when (viewHolder.itemViewType) {
                ADDSUBTASK -> {
                    viewHolder.name.apply {
                        text = getString(R.string.addSubtask)
                    }
                }
                REGULAR -> {
                    val subtask = task.subtasks[position]
                    viewHolder.name.text = subtask.title
                    viewHolder.checkbox!!.apply {
                        isChecked = subtask.done
                        setOnCheckedChangeListener { _, isChecked -> subtask.done = isChecked  }
                    }
                }
            }
        }

        override fun getItemCount() = task.subtasks.size + 1
    }

    override fun onFinishAssigneeDialog(assignee: User) {
        requireView().findViewById<AssigneeComponent>(R.id.assignee).user = assignee
        task.assignee = assignee
    }

    override fun onFinishTagDialog(tag: Tag) {
        if(!task.tags.contains(tag)) {
            task.tags.add(tag)
            tagsAdapter.notifyItemInserted(task.tags.size)
        } else {
            val i = task.tags.indexOf(tag)
            Toast.makeText(context, "Tag already added", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onFinishSectionDialog(section: Section) {
        requireView().findViewById<TextView>(R.id.sectionText).text = section.name
        task.section = section
    }
}
