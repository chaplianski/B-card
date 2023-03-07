package com.chaplianski.bcard.di

import android.app.Application

class DaggerApp: Application () {

    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent
            .builder()
            .context(context = this)
            .build()
    }
    fun getAppComponent() = appComponent
}