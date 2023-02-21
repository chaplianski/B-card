package com.chaplianski.bcard.core.helpers

import android.content.Context

interface LocaleHelper {
    fun onAttach(defaultLanguage: String? = null): Context
    fun getCurrentLocale(): String
    fun setCurrentLocale(language: String): Context
}