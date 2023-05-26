package com.example.projekt_am_2023.tag

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.projekt_am_2023.R

class EditTag : AppCompatActivity() {
    private lateinit var fragment: TagView
    private lateinit var tag: Tag

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_tag)

        tag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("tag", Tag::class.java)!!
        } else {
            intent.getSerializableExtra("tag") as Tag
        }

        fragment = TagView.newInstance(tag)
        supportFragmentManager.beginTransaction().replace(R.id.editTagFragment, fragment).commit()
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