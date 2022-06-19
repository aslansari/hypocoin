package com.aslansari.hypocoin.ui

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.aslansari.hypocoin.app.ActivityCompositionRoot
import com.aslansari.hypocoin.app.HypoCoinApp
import com.aslansari.hypocoin.app.ViewModelCompositionRoot

open class BaseActivity: AppCompatActivity() {

    protected val activityCompositionRoot by lazy {
        ActivityCompositionRoot((application as HypoCoinApp).appContainer)
    }

    val viewModelCompositionRoot by lazy {
        ViewModelCompositionRoot(activityCompositionRoot)
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun onStart() {
        super.onStart()
    }
}