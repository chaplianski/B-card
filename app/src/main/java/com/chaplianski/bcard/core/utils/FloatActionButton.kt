package com.chaplianski.bcard.core.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.floatingactionbutton.FloatingActionButton


//fun FloatingActionButton.animateWithTranslation(trX: Float, trY: Float){
//
//    val oa = ObjectAnimator.ofPropertyValuesHolder(
//        this,
//        PropertyValuesHolder.ofFloat("translationX", translationX, trX),
//        PropertyValuesHolder.ofFloat("translationY", translationY, trY)
//    ).apply {
//        duration = 300
//        interpolator = FastOutSlowInInterpolator()
//    }
//    oa.start()
//}
//
//fun FloatingActionButton.init(trX: Float, trY: Float){
//
//    visibility = View.INVISIBLE
//    addOnShowAnimationListener(object : AnimatorListenerAdapter(){
//        override fun onAnimationStart(animation: Animator?) {
//            animateWithTranslation(trX, trY)
//            super.onAnimationStart(animation)
//        }
//    })
//
//    addOnHideAnimationListener(object : AnimatorListenerAdapter(){
//        override fun onAnimationStart(animation: Animator?) {
//            animateWithTranslation(0f, 0f)
//            super.onAnimationStart(animation)
//        }
//    })
//}

