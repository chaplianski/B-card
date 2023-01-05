package com.chaplianski.bcard.core.helpers

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.chaplianski.bcard.R
import com.google.android.material.appbar.CollapsingToolbarLayout

class ButtonCardFragmentBehavior(): CoordinatorLayout.Behavior<FrameLayout>() {

    constructor(context: Context?, attrs: AttributeSet?) : this()

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FrameLayout,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        val collapsedToolbar = coordinatorLayout.findViewById<CollapsingToolbarLayout>(R.id.cards_fragment_collapsing_toolbar)
        Log.d("MyLog", "collapsing Tollbar translation = ${collapsedToolbar.y}, height = ${collapsedToolbar.height}")
        if (collapsedToolbar.y > collapsedToolbar.height*3/4){
            child.y = 0f
        }
    }
}