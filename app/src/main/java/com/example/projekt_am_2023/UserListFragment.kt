package com.example.projekt_am_2023

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.Serializable

private const val ARG_USERS = "users"

class UserListFragment : DialogFragment() {
    private lateinit var assignees: ArrayList<User>
    private lateinit var listener: AssigneeDialogListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        assignees = requireArguments().getSerializable(ARG_USERS) as ArrayList<User>
        listener = if(parentFragment == null) {
            requireContext() as AssigneeDialogListener
        } else {
            parentFragment as AssigneeDialogListener
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_list, container, false)

        view.findViewById<RecyclerView>(R.id.assigneeRecycler).apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = AssigneeAdapter(assignees)
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
        fun newInstance(users: ArrayList<User>) =
            UserListFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_USERS, users)
                }
            }
    }

    interface AssigneeDialogListener : Serializable {
        fun onFinishAssigneeDialog(assignee: User)
    }

    inner class AssigneeAdapter(private var assignees: List<User>) : RecyclerView.Adapter<AssigneeAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
            val view: AssigneeComponent
            init {
                this.view = view as AssigneeComponent
                this.view.setOnClickListener(this)
            }

            override fun onClick(view: View?) {
                listener?.onFinishAssigneeDialog(assignees[adapterPosition]);
                dismiss();
            }
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(AssigneeComponent(requireContext()))
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
                viewHolder.view.user = assignees[position]
        }

        override fun getItemCount() = assignees.size
    }
}
