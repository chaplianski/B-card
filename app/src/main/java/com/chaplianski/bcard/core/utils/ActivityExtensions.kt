package com.chaplianski.bcard.core.utils

import android.app.Activity
import android.graphics.Color

fun Activity.setWindowTransparency(
    listener: (statusBarSize: Int, navigationBarSize: Int) -> Unit = { _, _ -> }
) {
    InsetUtil.removeSystemInsets(window.decorView, listener)
    window.navigationBarColor = Color.TRANSPARENT
    window.statusBarColor = Color.TRANSPARENT
}