package com.example.projekt_am_2023.tag

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
import androidx.core.view.updateLayoutParams
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

class TagListFragment : DialogFragment() {
    private var tags: MutableList<Tag> = mutableListOf()
    private lateinit var listener: TagDialogListener
    private lateinit var tagAdapter: TagAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listener = if(parentFragment == null) {
            requireContext() as TagDialogListener
        } else {
            parentFragment as TagDialogListener
        }

        DatabaseLoader.tags.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                tags = Tag.loadDatabaseArray(dataSnapshot)
                tagAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("Firebase", "tags - loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tag_list, container, false)

        tagAdapter = TagAdapter()
        view.findViewById<RecyclerView>(R.id.tagRecycler).apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = tagAdapter
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
        fun newInstance() = TagListFragment().apply {
            arguments = Bundle().apply { }
        }
    }

    interface TagDialogListener : Serializable {
        fun onFinishTagDialog(tag: Tag)
    }

    inner class TagAdapter() : RecyclerView.Adapter<TagAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
            init {
                view.setOnClickListener(this)
            }

            override fun onClick(view: View?) {
                listener.onFinishTagDialog(tags[adapterPosition]);
                dismiss();
            }
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(TagComponent(viewGroup.context))
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            (viewHolder.itemView as TagComponent).apply{
                layoutParams = (layoutParams as ViewGroup.MarginLayoutParams).apply {
                    updateMargins(bottom = (10 * resources.displayMetrics.density).toInt())
                    updateLayoutParams<ViewGroup.LayoutParams> {
                        width = ViewGroup.LayoutParams.MATCH_PARENT
                    }
                }
                setTag(tags[position])
            }
        }

        override fun getItemCount() = tags.size
    }
}