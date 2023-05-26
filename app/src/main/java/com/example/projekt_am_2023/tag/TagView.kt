package com.example.projekt_am_2023.tag

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.projekt_am_2023.R
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.skydoves.colorpickerview.preference.ColorPickerPreferenceManager

private const val ARG_TAG = "tag"

class TagView: Fragment() {
    private lateinit var tagPreview: TagComponent
    private lateinit var tag: Tag

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tag = if(arguments == null) {
            Tag("", Color.BLACK)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getSerializable(ARG_TAG, Tag::class.java)!!
        } else {
            requireArguments().getSerializable(ARG_TAG)!! as Tag
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tag_view, container, false)

        tagPreview = view.findViewById<TagComponent>(R.id.previewDisplay).apply {
            setTag(this@TagView.tag)
        }

        view.findViewById<EditText>(R.id.nameText).apply {
            setText(this@TagView.tag.name)
            addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

                override fun afterTextChanged(s: Editable?) {
                    this@TagView.tag.name = s.toString()
                    tagPreview.name = this@TagView.tag.name
                }
            })
        }

        val pickerName = "TagColorPicker"
        ColorPickerPreferenceManager.getInstance(context).setColor(pickerName, tag.color)
        view.findViewById<ImageView>(R.id.colorPreview).apply {
            setColorFilter(this@TagView.tag.color)
            setOnClickListener {
                ColorPickerDialog.Builder(context)
                    .setTitle(getString(R.string.tagColorPicker))
                    .setPreferenceName(pickerName)
                    .setPositiveButton(
                        getString(R.string.confirm),
                        ColorEnvelopeListener { envelope, _ ->
                            this@TagView.tag.color = envelope.color
                            tagPreview.background = envelope.color
                            this.setColorFilter(envelope.color)
                        })
                    .setNegativeButton(getString(R.string.cancel)) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    .attachAlphaSlideBar(false)
                    .show()
            }
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(tag: Tag) = TagView().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_TAG, tag)
            }
        }
    }
}