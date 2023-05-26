package com.example.projekt_am_2023.tag

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekt_am_2023.R
import com.example.projekt_am_2023.database.DatabaseLoader
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.io.Serializable

class TagListFragment : Fragment() {
    private var tags: MutableList<Tag> = mutableListOf()
    private lateinit var tagAdapter: TagAdapter
    private lateinit var listener: TagListListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        listener = if(parentFragment == null) {
            requireContext() as TagListListener
        } else {
            parentFragment as TagListListener
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

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = TagListFragment().apply {
            arguments = Bundle().apply { }
        }
    }

    inner class TagAdapter() : RecyclerView.Adapter<TagAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
            init {
                view.setOnClickListener(this)
            }

            override fun onClick(view: View?) {
                listener.onTagSelected(tags[adapterPosition]);
            }
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(TagComponent(viewGroup.context))
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            (viewHolder.itemView as TagComponent).apply {
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

    interface TagListListener : Serializable {
        fun onTagSelected(tag: Tag)
    }

}