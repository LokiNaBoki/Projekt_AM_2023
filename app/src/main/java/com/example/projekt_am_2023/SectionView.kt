package com.example.projekt_am_2023

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

private const val ARG_SECTION = "section"

class SectionView : Fragment() {
    private lateinit var section: Section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        section = if(arguments == null) {
            Section()
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getSerializable(ARG_SECTION, Section::class.java)!!
        } else {
            requireArguments().getSerializable(ARG_SECTION)!! as Section
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_section_view, container, false)

        view.findViewById<EditText>(R.id.nameText).apply {
            setText(this@SectionView.section.name)
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

                override fun afterTextChanged(s: Editable?) {
                    this@SectionView.section.name = s.toString()
                }
            })
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(section: Section) = SectionView().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_SECTION, section)
                }
            }
    }
}