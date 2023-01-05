package com.chaplianski.bcard.core.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.Checkable
import com.google.android.material.floatingactionbutton.FloatingActionButton


class CustomFab @JvmOverloads constructor(context: Context, attr: AttributeSet? = null, defStyleAttr: Int = 0) :
    FloatingActionButton(context, attr, defStyleAttr), Checkable {

    private var checked: Boolean = false

    override fun setChecked(check: Boolean) {
        if (checked == check) return
        checked = check
//        playAnnimation()
    }

    private fun playAnnimation() {
    }

    override fun isChecked(): Boolean = checked

    override fun toggle() {
        isChecked = !checked
    }

    override fun performClick(): Boolean {
        toggle()
        return super.performClick()
    }
}