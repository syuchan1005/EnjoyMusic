package com.github.syuchan1005.enjoymusic

import com.github.syuchan1005.enjoymusic.di.DaggerAppComponent
import com.github.syuchan1005.enjoymusic.di.applyAutoInjector
import dagger.android.support.DaggerApplication

class App : DaggerApplication() {
    override fun applicationInjector() = DaggerAppComponent.builder()
            .application(this)
            .build()

    override fun onCreate() {
        super.onCreate()
        applyAutoInjector()
    }

    override fun onTerminate() {
        super.onTerminate()
    }

}
