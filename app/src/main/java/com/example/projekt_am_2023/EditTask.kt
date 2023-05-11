package com.example.projekt_am_2023

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast

class EditTask : AppCompatActivity() {
    private lateinit var task: Task
    private lateinit var fragment: TaskView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        task = intent.extras!!.getSerializable("task") as Task
        fragment = TaskView.newInstance(task)
        supportFragmentManager.beginTransaction().replace(R.id.editTaskFragment, fragment).commit()
    }

    fun onSave(view: View) {
        Toast.makeText(this, "Task Saved - '${fragment.getTask().title}'", Toast.LENGTH_SHORT).show()
        finish()
    }

    fun onCancel(view: View) {
        Toast.makeText(this, "Task edit canceled", Toast.LENGTH_SHORT).show()
        finish()
    }
}