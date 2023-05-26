package com.example.projekt_am_2023.user

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
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekt_am_2023.database.DatabaseLoader
import com.example.projekt_am_2023.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.io.Serializable

class UserDialog : DialogFragment() {
    private var assignees: MutableList<User> = mutableListOf()
    private lateinit var listener: AssigneeDialogListener
    private lateinit var assigneeAdapter: AssigneeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listener = if(parentFragment == null) {
            requireContext() as AssigneeDialogListener
        } else {
            parentFragment as AssigneeDialogListener
        }

        DatabaseLoader.users.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                assignees = User.loadDatabaseArray(dataSnapshot)
                assigneeAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("Firebase", "users - loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_user, container, false)

        assigneeAdapter = AssigneeAdapter()
        view.findViewById<RecyclerView>(R.id.assigneeRecycler).apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = assigneeAdapter
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
        fun newInstance() = UserDialog().apply {
            arguments = Bundle().apply { }
        }
    }

    interface AssigneeDialogListener : Serializable {
        fun onFinishAssigneeDialog(assignee: User)
    }

    inner class AssigneeAdapter : RecyclerView.Adapter<AssigneeAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
            val view: UserComponent
            init {
                this.view = view as UserComponent
                this.view.setOnClickListener(this)
            }

            override fun onClick(view: View?) {
                listener.onFinishAssigneeDialog(assignees[adapterPosition])
                dismiss()
            }
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(UserComponent(requireContext()))
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
                viewHolder.view.user = assignees[position]
        }

        override fun getItemCount() = assignees.size
    }
}
