package com.chaplianski.bcard.presenter.utils

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HideFabBehaviour(): CoordinatorLayout.Behavior<FloatingActionButton>() {
    constructor(context: Context, attributeSet: AttributeSet): this()

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: FloatingActionButton,
        dependency: View
    ): Boolean {
        dependency as CustomFab
        return if (dependency.isChecked && dependency.isOrWillBeHidden) {
            child.hide()
            true
        }
        else if (dependency.isChecked) {
            child.show()
            true
        }
        else false
    }

}