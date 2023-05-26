package com.example.projekt_am_2023.user

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.projekt_am_2023.R

class UserComponent : LinearLayout {
    private lateinit var avatar: ImageView
    private lateinit var name: TextView
    private var _user: User? = null
    var user: User?
        get() = _user
        set(value) {
            _user = value
            updateUser()
        }

    constructor(context: Context)
            : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet)
            : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int)
            : super(context, attrs, defStyle) {
        init()
    }

    private fun init() {
        inflate(context, R.layout.component_user, this)

        avatar = findViewById(R.id.userAvatar)
        name = findViewById(R.id.userName)

        updateUser()
    }

    private fun updateUser() {
        if(user == null || user!!.avatar == null) {
            val drawable = ContextCompat.getDrawable(context, R.drawable.user_default)
            avatar.setImageDrawable(drawable)
        } else {
            user!!.loadAvatar(context, avatar)
        }
        name.apply{ text = user?.name ?: "" }
    }
}