package com.chaplianski.bcard.core.utils

import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager

fun changeStatusBarContrastStyle(window: Window, lightIcons: Boolean) {
    val decorView: View = window.decorView

    if (lightIcons) {
        // Draw light icons on a dark background color
        Log.d("MyLog", "light icons")
        window.apply {
//            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility =
//                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN and
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
//        decorView.systemUiVisibility =
//            decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
    } else {
            window.apply {
//                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                decorView.systemUiVisibility =
//                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN and
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
//        // Draw dark icons on a light background color
//        Log.d("MyLog", "dark icons")
////        decorView.systemUiVisibility =
//            decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//        decorView.setSystemUiVisibility(0)



}