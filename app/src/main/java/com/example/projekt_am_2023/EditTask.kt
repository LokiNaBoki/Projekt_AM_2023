package com.example.projekt_am_2023

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

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
        val users = intent.extras!!.getSerializable("users") as ArrayList<User>

        fragment = TaskView.newInstance(task, users)
        supportFragmentManager.beginTransaction().replace(R.id.editTaskFragment, fragment).commit()
    }

    fun onSave(ignoredView: View) {
        val result = Intent().apply {
            putExtra("task", task)
            putExtra("index", index)
            putExtra("section", section)
        }
        setResult(Activity.RESULT_OK, result);
        finish()
    }

    fun onCancel(ignoredView: View) {
        setResult(Activity.RESULT_CANCELED, null);
        finish()
    }
}