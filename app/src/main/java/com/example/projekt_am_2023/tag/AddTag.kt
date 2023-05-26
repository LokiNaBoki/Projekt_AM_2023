package com.example.projekt_am_2023.tag

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.projekt_am_2023.R

class AddTag : AppCompatActivity() {
    private lateinit var fragment: TagView
    private val tag: Tag = Tag("", Color.BLACK)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tag)

        fragment = TagView.newInstance(tag)
        supportFragmentManager.beginTransaction().replace(R.id.addTagFragment, fragment).commit()
    }

    fun onSave(ignoredView: View) {
        if(tag.name.isEmpty()) {
            setResult(Activity.RESULT_CANCELED)
        } else {
            tag.saveDatabase()
            val intent = Intent().apply {
                putExtra("tag", tag)
            }
            setResult(Activity.RESULT_OK, intent)
        }

        finish()
    }

    fun onCancel(ignoredView: View) {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}