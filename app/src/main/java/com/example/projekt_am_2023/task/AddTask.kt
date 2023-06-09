package com.example.projekt_am_2023.task

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projekt_am_2023.R

class AddTask: AppCompatActivity() {
    private lateinit var fragment: TaskView
    private val task: Task = Task()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_task)

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
            }
            setResult(Activity.RESULT_OK, result)
            finish()
        }
    }

    fun onCancel(ignoredView: View) {
        setResult(Activity.RESULT_CANCELED, null)
        finish()
    }
}