package com.chaplianski.bcard.presenter.helpers

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.chaplianski.bcard.R
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout

class CardFragmentBehavior (): CoordinatorLayout.Behavior<FrameLayout>() {

    constructor(context: Context?, attrs: AttributeSet?) : this()

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
        val appbar: AppBarLayout = coordinatorLayout.findViewById(R.id.appbar_cards_fragment)

//        Log.d("MyLog", "move  = ${dy}")
        when {
            newTranslation > child.height -> child.translationY = child.height.toFloat()
            newTranslation < 0 -> child.translationY = 0f
            appbar.translationY < 1/2*appbar.height -> child.translationY = child.height.toFloat()
            else -> child.translationY = newTranslation
        }
    }

    override fun onStopNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FrameLayout,
        target: View
    ) {

//        val childTranslation = child.translationY
//        if (appbar.translationY < 1/2*appbar.height){
//            child.translationY = child.height.toFloat()
//        }

//        if (childTranslation < 1/2*child.height){
//             child.translationY = 80f
//         }


//        super.onStopNestedScroll(coordinatorLayout, child, target)
    }
}