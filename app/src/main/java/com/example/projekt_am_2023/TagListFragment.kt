package com.example.projekt_am_2023

import android.content.res.ColorStateList
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
import android.widget.TextView
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.Serializable

private const val ARG_TAGS = "tags"

class TagListFragment : DialogFragment() {
    private lateinit var tags: ArrayList<Tag>
    private lateinit var listener: TagDialogListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tags = requireArguments().getSerializable(ARG_TAGS) as ArrayList<Tag>
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
        val view = inflater.inflate(R.layout.fragment_tag_list, container, false)

        view.findViewById<RecyclerView>(R.id.tagRecycler).apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = TagAdapter()
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
        fun newInstance(tags: ArrayList<Tag>) =
            TagListFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_TAGS, tags)
                }
            }
    }

    interface TagDialogListener : Serializable {
        fun onFinishTagDialog(tag: Tag)
    }

    inner class TagAdapter() : RecyclerView.Adapter<TagAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
            val name: TextView
            init {
                this.name = view.findViewById(R.id.tagName)
                this.name.setOnClickListener(this)
            }

            override fun onClick(view: View?) {
                listener?.onFinishTagDialog(tags[adapterPosition]);
                dismiss();
            }
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.tag, viewGroup, false).apply {
                    layoutParams = (layoutParams as ViewGroup.MarginLayoutParams).apply {
                        updateMargins(bottom = (10 * resources.displayMetrics.density).toInt())
                        updateLayoutParams<ViewGroup.LayoutParams> {
                            height = ViewGroup.LayoutParams.WRAP_CONTENT
                        }
                    }
                })
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.name.apply {
                text = tags[position].name
                backgroundTintList = ColorStateList.valueOf(tags[position].color)
            }
        }

        override fun getItemCount() = tags.size
    }
}