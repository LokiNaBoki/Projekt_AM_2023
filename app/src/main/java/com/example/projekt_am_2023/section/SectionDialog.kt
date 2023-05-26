package com.example.projekt_am_2023.section

import android.content.res.Configuration
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

class SectionDialog: DialogFragment(), SectionListFragment.SectionListListener {
    private lateinit var listener: SectionDialogListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listener = if(parentFragment == null) {
            requireContext() as SectionDialogListener
        } else {
            parentFragment as SectionDialogListener
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_section, container, false)

        val fragment = SectionListFragment.newInstance()
        childFragmentManager.beginTransaction().replace(R.id.sectionFragment, fragment).commit()

        view.findViewById<Button>(R.id.cancel).setOnClickListener { dismiss() }

        return view
    }

    override fun onResume() {
        val widthPart = 0.8f
        val heightPart = if(requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            0.9f
        } else {
            0.6f
        }


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = requireActivity().windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())

            val width = windowMetrics.bounds.width() - insets.left - insets.right
            val height = windowMetrics.bounds.height() - insets.top - insets.bottom

            dialog!!.window?.apply {
                setLayout(
                    (width * widthPart).toInt(),
                    (height * heightPart).toInt()
                )
                setGravity(Gravity.CENTER)
            }
        } else {
            val size = Point()

            dialog!!.window.apply {
                this!!.windowManager.defaultDisplay.getSize(size)
                setLayout(
                    (size.x * widthPart).toInt(),
                    (size.y * heightPart).toInt(),
                )
                setGravity(Gravity.CENTER)
            }
        }

        super.onResume()
    }

    companion object {
        @JvmStatic
        fun newInstance() = SectionDialog().apply {
            arguments = Bundle().apply { }
        }
    }

    interface SectionDialogListener: Serializable {
        fun onFinishSectionDialog(section: Section)
    }

    override fun onSectionSelected(section: Section) {
        listener.onFinishSectionDialog(section)
        dismiss()
    }
}