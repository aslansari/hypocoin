package com.aslansari.hypocoin.app

import android.app.Application
import com.aslansari.hypocoin.BuildConfig
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree

@HiltAndroidApp
class HypoCoinApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeTimber()
        FirebaseApp.initializeApp(this)
    }

    private fun initializeTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(object : DebugTree() {
                // Add the line number to the tag
                override fun createStackElementTag(element: StackTraceElement): String {
                    return String.format(
                        "[Line - %s] [Method - %s] [Class - %s]",
                        element.lineNumber,
                        element.methodName,
                        super.createStackElementTag(element)
                    )
                }
            })
        }
    }
}