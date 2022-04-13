package com.aslansari.hypocoin.repository

import android.app.Service
import com.aslansari.hypocoin.app.ActivityCompositionRoot
import com.aslansari.hypocoin.app.HypoCoinApp
import com.aslansari.hypocoin.app.ViewModelCompositionRoot

abstract class BaseService: Service() {

    protected val activityCompositionRoot by lazy {
        ActivityCompositionRoot((application as HypoCoinApp).appContainer)
    }

    val viewModelCompositionRoot by lazy {
        ViewModelCompositionRoot(activityCompositionRoot)
    }
}