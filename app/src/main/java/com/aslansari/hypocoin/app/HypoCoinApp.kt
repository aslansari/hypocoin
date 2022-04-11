package com.aslansari.hypocoin.app

import android.app.Application
import com.aslansari.hypocoin.app.AppContainer
import com.aslansari.hypocoin.app.HypoCoinApp
import com.aslansari.hypocoin.BuildConfig
import timber.log.Timber
import timber.log.Timber.DebugTree

class HypoCoinApp : Application() {
    @JvmField
    var appContainer: AppContainer? = null
    override fun onCreate() {
        super.onCreate()
        instance = this
        appContainer = AppContainer(this)
        initializeTimber()
    }

    private fun initializeTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(object : DebugTree() {
                // Add the line number to the tag
                override fun createStackElementTag(element: StackTraceElement): String? {
                    return String.format("[Line - %s] [Method - %s] [Class - %s]",
                        element.lineNumber,
                        element.methodName,
                        super.createStackElementTag(element))
                }
            })
        }
    }

    companion object {
        var instance: HypoCoinApp? = null
            private set
    }
}