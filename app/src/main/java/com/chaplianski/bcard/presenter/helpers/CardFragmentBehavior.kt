package com.chaplianski.bcard.presenter.helpers

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.chaplianski.bcard.R
import com.google.android.material.appbar.CollapsingToolbarLayout

class CardFragmentBehavior (): CoordinatorLayout.Behavior<FrameLayout>() {

    constructor(context: Context?, attrs: AttributeSet?) : this()
    var beginChildPosition = 0f

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FrameLayout,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return child.id == R.id.fl_cards_fragment_top_info && axes == View.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FrameLayout,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        val oldTranslation = child.translationY
        val newTranslation = oldTranslation + dy
//        Log.d("MyLog", "move  = ${dy}")
        when {
            newTranslation > child.height -> child.translationY = child.height.toFloat()
            newTranslation < 0 -> child.translationY = 0f
            else -> child.translationY = newTranslation
        }
    }

//    override fun onStopNestedScroll(
//        coordinatorLayout: CoordinatorLayout,
//        child: FrameLayout,
//        target: View,
//        type: Int
//    ) {
//
//        val collapsedToolbar = coordinatorLayout.findViewById<CollapsingToolbarLayout>(R.id.cards_fragment_collapsing_toolbar)
//
//        if (collapsedToolbar.y > collapsedToolbar.height*3/4){
//            child.y = 0f
//        }
//
//
//
//
//    }


}