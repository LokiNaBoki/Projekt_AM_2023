package com.example.projekt_am_2023.section

import android.graphics.Color
import android.graphics.Insets
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.Button
import android.widget.TextView
import androidx.core.view.updateMargins
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekt_am_2023.database.DatabaseLoader
import com.example.projekt_am_2023.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.io.Serializable

class SectionDialog : DialogFragment() {
    private var sections: MutableList<Section> = mutableListOf()
    private lateinit var listener: SectionDialogListener
    private lateinit var sectionAdapter: SectionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listener = if(parentFragment == null) {
            requireContext() as SectionDialogListener
        } else {
            parentFragment as SectionDialogListener
        }

        DatabaseLoader.sections.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                sections = Section.loadDatabaseArray(dataSnapshot)
                sectionAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("Firebase", "sections - loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_section, container, false)

        sectionAdapter = SectionAdapter()
        view.findViewById<RecyclerView>(R.id.sectionRecycler).apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = sectionAdapter
        }

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
        fun newInstance() = SectionDialog().apply {
            arguments = Bundle().apply { }
        }
    }

    interface SectionDialogListener : Serializable {
        fun onFinishSectionDialog(section: Section)
    }

    inner class SectionAdapter() : RecyclerView.Adapter<SectionAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
            val text: TextView
            init {
                text = view as TextView
                text.setOnClickListener(this)
            }

            override fun onClick(view: View?) {
                listener.onFinishSectionDialog(sections[adapterPosition]);
                dismiss();
            }
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(TextView(viewGroup.context).apply {
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    updateMargins(bottom = (10 * resources.displayMetrics.density).toInt())
                }
                textSize = 22f
                setTextColor(Color.BLACK)
            })
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.text.text = sections[position].name
        }

        override fun getItemCount() = sections.size
    }
}