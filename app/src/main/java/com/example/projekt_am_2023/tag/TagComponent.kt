package com.example.projekt_am_2023.tag

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.example.projekt_am_2023.R
import kotlin.math.pow

class TagComponent : LinearLayout {
    private lateinit var text: TextView
    private var foreground: Int = Color.WHITE
    private var _background: Int = Color.BLACK
    var background: Int
        get() = _background
        set(value) {
            _background = value
            setContrastingForeground()
            invalidateColors()
        }

    private var _name: String = "-"
    var name: String
        get() = _name
        set(value) {
            _name = value
            invalidateName()
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
        inflate(context, R.layout.component_tag, this)

        layoutParams = MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        text = findViewById(R.id.tagName)

        invalidateColors()
        invalidateName()
    }

    fun setTag(tag: Tag) {
        background = tag.color
        name = tag.name
    }

    private fun invalidateColors() {
        text.apply {
            setTextColor(this@TagComponent.foreground)
            //setBackgroundColor(this@TagComponent.background)
            backgroundTintList = ColorStateList.valueOf(this@TagComponent.background)
        }
    }

    private fun invalidateName() {
        text.text = name
    }

    // https://www.w3.org/TR/2008/REC-WCAG20-20081211/#relativeluminancedef
    private fun relativeLuminance(color: Int): Double {
        fun component(c: Float): Double {
            return if (c <= 0.03928) {
                c / 12.92
            } else {
                ((c + 0.055) / 1.055).pow(2.4)
            }
        }

        val R = component(color.red / 255f)
        val G = component(color.green / 255f)
        val B = component(color.blue / 255f)

        return 0.2126 * R + 0.7152 * G + 0.0722 * B
    }

    // https://www.w3.org/TR/UNDERSTANDING-WCAG20/visual-audio-contrast-contrast.html#key-terms
    private fun setContrastingForeground() {
        val backgroundRelLum = relativeLuminance(background)
        val darkRelativeLuminance = 1.0f
        val lightRelativeLuminance = 0.0f

        val onDark = (backgroundRelLum + 0.05) / (darkRelativeLuminance + 0.05)
        val onLight = (lightRelativeLuminance + 0.05) / (backgroundRelLum + 0.05)

        foreground = if(onDark > onLight) { Color.BLACK } else { Color.WHITE }
    }
}