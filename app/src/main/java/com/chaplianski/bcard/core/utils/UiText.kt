package com.chaplianski.bcard.core.utils

import android.content.Context
import androidx.annotation.StringRes

sealed class UiText {
    data class DynamicString(val value: String): UiText()
    class StringResource(
        @StringRes val res: Int,
        vararg val args: Any
    ): UiText()

    fun asString(context: Context): String {
        return when(this){
            is DynamicString -> value
            is StringResource -> context.getString(res,*args)
        }
    }
}