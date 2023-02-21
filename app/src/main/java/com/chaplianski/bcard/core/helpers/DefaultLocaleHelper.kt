package com.chaplianski.bcard.core.helpers

import android.content.Context
import java.util.*

class DefaultLocaleHelper private constructor(context: Context) : BaseLocaleHelper(context) {
    companion object {
        @Volatile
        private var instance: LocaleHelper? = null
        private var LOCK: Any = Any()

        fun getInstance(context: Context): LocaleHelper {
            synchronized(LOCK) {
                if (instance == null) instance = DefaultLocaleHelper(context)
                return instance!!
            }
        }
    }

    override fun setCurrentLocale(language: String): Context {
        return setLocale(context, language)
    }

    override fun onAttach(defaultLanguage: String?): Context {
        val lang = getPersistedLocale(defaultLanguage ?: Locale.getDefault().language)
        return setLocale(context, lang)
    }

    override fun getCurrentLocale(): String =
        getPersistedLocale(Locale.getDefault().language)
}