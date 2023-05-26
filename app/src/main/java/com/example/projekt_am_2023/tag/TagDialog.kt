package com.example.projekt_am_2023.tag

import android.graphics.Insets
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.projekt_am_2023.R
import java.io.Serializable

class TagDialog : DialogFragment(), TagListFragment.TagListListener {
    private lateinit var listener: TagDialogListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listener = if(parentFragment == null) {
            requireContext() as TagDialogListener
        } else {
            parentFragment as TagDialogListener
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_tag, container, false)

        val fragment = TagListFragment.newInstance()
        childFragmentManager.beginTransaction().replace(R.id.tagFragment, fragment).commit()

        view.findViewById<Button>(R.id.cancel).setOnClickListener { dismiss() }

        return view
    }

    override fun onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = requireActivity().windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())

            val width = windowMetrics.bounds.width() - insets.left - insets.right
            val height = windowMetrics.bounds.height() - insets.top - insets.bottom

            dialog!!.window?.apply {
                setLayout( (width * 0.8).toInt(), (height * 0.6).toInt() )
                setGravity(Gravity.CENTER)
            }
        } else {
            val size = Point()

            dialog!!.window.apply {
                this!!.windowManager.defaultDisplay.getSize(size)
                setLayout(
                    (size.x * 0.8).toInt(),
                    (size.y * 0.6).toInt(),
                )
                setGravity(Gravity.CENTER)
            }
        }

        super.onResume()
    }

    companion object {
        @JvmStatic
        fun newInstance() = TagDialog().apply {
            arguments = Bundle().apply { }
        }
    }

    interface TagDialogListener : Serializable {
        fun onFinishTagDialog(tag: Tag)
    }

    override fun onTagSelected(tag: Tag) {
        listener.onFinishTagDialog(tag)
        dismiss()
    }
}