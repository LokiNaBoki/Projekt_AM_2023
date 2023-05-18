package com.example.projekt_am_2023

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddTask : AppCompatActivity() {
    private lateinit var fragment: TaskView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_task)

        val users = intent.extras!!.getSerializable("users") as ArrayList<User>

        fragment = TaskView.newInstance(Task.emptyTask(), users)
        supportFragmentManager.beginTransaction().replace(R.id.editTaskFragment, fragment).commit()
    }

    fun onSave(ignoredView: View) {
        Toast.makeText(this, "Task Added - '${fragment.getTask().title}'", Toast.LENGTH_SHORT).show()
        finish()
    }

    fun onCancel(ignoredView: View) {
        Toast.makeText(this, "Task add canceled", Toast.LENGTH_SHORT).show()
        finish()
    }
}