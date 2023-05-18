package com.example.projekt_am_2023

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class AddTask : AppCompatActivity() {
    private lateinit var fragment: TaskView
    private val task: Task = Task.emptyTask()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_task)

        val users = intent.extras!!.getSerializable("users") as ArrayList<User>

        fragment = TaskView.newInstance(task, users)
        supportFragmentManager.beginTransaction().replace(R.id.editTaskFragment, fragment).commit()
    }

    fun onSave(ignoredView: View) {
        val result = Intent().apply {
            putExtra("task", task)
        }
        setResult(Activity.RESULT_OK, result)
        finish()
    }

    fun onCancel(ignoredView: View) {
        setResult(Activity.RESULT_CANCELED, null)
        finish()
    }
}