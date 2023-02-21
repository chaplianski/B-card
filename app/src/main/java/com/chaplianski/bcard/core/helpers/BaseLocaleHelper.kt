package com.chaplianski.bcard.core.helpers

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.LocaleList
import android.preference.PreferenceManager
import java.util.*

abstract class BaseLocaleHelper(internal val context: Context) :
    LocaleHelper {
    companion object {
        private const val SELECTED_LANGUAGE = "LocaleHelperKt_SelectedLanguage"
    }

    internal fun getPersistedLocale(defaultLocale: String): String {
        return cacheStorage.getString(SELECTED_LANGUAGE, defaultLocale) ?: defaultLocale
    }

    private val cacheStorage: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(
            context
        )
    }

    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }

    private fun baseSetLocale(context: Context, newLocale: Locale): Context {
        var tmpContext = context
        val res = tmpContext.resources
        val configuration = res.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(newLocale)
            val localeList = LocaleList(newLocale)
            LocaleList.setDefault(localeList)
            configuration.setLocales(localeList)
            tmpContext = tmpContext.createConfigurationContext(configuration)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(newLocale)
            res.updateConfiguration(configuration, res.displayMetrics)
        } else {
            configuration.locale = newLocale
            res.updateConfiguration(configuration, res.displayMetrics)
        }
        return tmpContext
    }

    internal fun setLocale(context: Context, newLocale: String): Context {
        cacheStorage.edit().apply {
            putString(SELECTED_LANGUAGE, newLocale)
            apply()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, newLocale)
        }
        val locale = Locale(newLocale)
        return baseSetLocale(context, locale)
    }
}
