package com.chaplianski.bcard.core.utils

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator

class ViewAnimation {


}

fun View.animateVisibility(setVisible: Boolean) {
//    if (setVisible) expand(this) else collapse(this)
    if (setVisible) expand(this) else collapseWidth(this)
}

private fun expand(view: View) {
    view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    val initialWidth = 0
    val targetWidth = 660 //view.measuredWidth

    // Older versions of Android (pre API 21) cancel animations for views with a height of 0.
    //v.getLayoutParams().height = 1;
    view.layoutParams.width = 0
    view.visibility = View.VISIBLE


//    val initialHeight = 0
//    val targetHeight = view.measuredHeight
//
//    // Older versions of Android (pre API 21) cancel animations for views with a height of 0.
//    //v.getLayoutParams().height = 1;
//    view.layoutParams.height = 0
//    view.visibility = View.VISIBLE

//    animateView(view, initialHeight, targetHeight)

    animateWidthView(view, initialWidth, targetWidth)
}

private fun collapse(view: View) {
    val initialHeight = view.measuredHeight
    val targetHeight = 0

    animateView(view, initialHeight, targetHeight)
}

private fun collapseWidth(view: View) {
    val initialWidth = view.measuredWidth
    val targetWidth = 0

    animateView(view, initialWidth, targetWidth)
}

private fun animateView(v: View, initialHeight: Int, targetHeight: Int) {
    val valueAnimator = ValueAnimator.ofInt(initialHeight, targetHeight)
    valueAnimator.addUpdateListener { animation ->
        v.layoutParams.height = animation.animatedValue as Int
        v.requestLayout()
    }
    valueAnimator.addListener(object : Animator.AnimatorListener {
        override fun onAnimationEnd(animation: Animator) {
            v.layoutParams.height = targetHeight
        }

        override fun onAnimationStart(animation: Animator) {}
        override fun onAnimationCancel(animation: Animator) {}
        override fun onAnimationRepeat(animation: Animator) {}
    })
    valueAnimator.duration = 300
    valueAnimator.interpolator = DecelerateInterpolator()
    valueAnimator.start()
}

private fun animateWidthView(v: View, initialWidth: Int, targetWidth: Int) {
    val valueAnimator = ValueAnimator.ofInt(initialWidth, targetWidth)
    valueAnimator.addUpdateListener { animation ->
        v.layoutParams.width = animation.animatedValue as Int
        v.requestLayout()
    }
    valueAnimator.addListener(object : Animator.AnimatorListener {
        override fun onAnimationEnd(animation: Animator) {
            v.layoutParams.width = targetWidth
            v.translationX = -30f
        }

        override fun onAnimationStart(animation: Animator) {}
        override fun onAnimationCancel(animation: Animator) {}
        override fun onAnimationRepeat(animation: Animator) {}
    })
    valueAnimator.duration = 300
    valueAnimator.interpolator = DecelerateInterpolator()
    valueAnimator.start()
}