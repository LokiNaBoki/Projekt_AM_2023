package com.example.projekt_am_2023.section

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.projekt_am_2023.R

class AddSection : AppCompatActivity() {
    private lateinit var fragment: SectionView
    private val section: Section = Section()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_section)

        fragment = SectionView.newInstance(section)
        supportFragmentManager.beginTransaction().replace(R.id.addSectionFragment, fragment).commit()
    }

    fun onSave(ignoredView: View) {
        if(section.name.isEmpty()) {
            setResult(Activity.RESULT_CANCELED)
        } else {
            section.saveDatabase()
            val intent = Intent().apply {
                putExtra("section", section)
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