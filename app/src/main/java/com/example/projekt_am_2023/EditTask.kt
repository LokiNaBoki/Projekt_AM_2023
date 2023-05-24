package com.example.projekt_am_2023

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

class EditTask : AppCompatActivity() {
    private var index: Int = 0
    private var section: Int = 0
    private lateinit var task: Task
    private lateinit var fragment: TaskView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        task = intent.extras!!.getSerializable("task") as Task
        index = intent.extras!!.getInt("index")
        section = intent.extras!!.getInt("section")

        fragment = TaskView.newInstance(task)
        supportFragmentManager.beginTransaction().replace(R.id.editTaskFragment, fragment).commit()
    }

    fun onSave(ignoredView: View) {
        if(task.title.isEmpty()) {
            Toast.makeText(this, "Task's title cannot be empty", Toast.LENGTH_SHORT).show()
        } else if(task.section == null) {
            Toast.makeText(this, "Task's section cannot be empty", Toast.LENGTH_SHORT).show()
        } else {
            task.saveDatabase()
            val result = Intent().apply {
                putExtra("task", task)
                putExtra("index", index)
                putExtra("section", section)
            }
            setResult(Activity.RESULT_OK, result);
            finish()
        }
    }

    fun onCancel(ignoredView: View) {
        setResult(Activity.RESULT_CANCELED, null);
        finish()
    }
}