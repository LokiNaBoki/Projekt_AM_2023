package com.example.projekt_am_2023.section

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.projekt_am_2023.R

class EditSection: AppCompatActivity() {
    private lateinit var fragment: SectionView
    private lateinit var section: Section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_section)

        section = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("section", Section::class.java)!!
        } else {
            intent.getSerializableExtra("section") as Section
        }

        fragment = SectionView.newInstance(section)
        supportFragmentManager.beginTransaction().replace(R.id.editSectionFragment, fragment).commit()
    }

    fun onSave(ignoredView: View) {
        if(section.name.isEmpty()) {
            Toast.makeText(this, "Section's name cannot be empty", Toast.LENGTH_SHORT).show()
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