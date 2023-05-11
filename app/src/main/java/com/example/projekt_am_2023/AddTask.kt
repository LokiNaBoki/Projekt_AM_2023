package com.example.projekt_am_2023

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

class AddTask : AppCompatActivity() {
    private lateinit var fragment: TaskView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_task)

        fragment = TaskView.newInstance(Task.emptyTask())
        supportFragmentManager.beginTransaction().replace(R.id.editTaskFragment, fragment).commit()
    }

    fun onSave(view: View) {
        Toast.makeText(this, "Task Added - '${fragment.getTask().title}'", Toast.LENGTH_SHORT).show()
        finish()
    }

    fun onCancel(view: View) {
        Toast.makeText(this, "Task add canceled", Toast.LENGTH_SHORT).show()
        finish()
    }
}