package com.aslansari.hypocoin.ui

import androidx.fragment.app.Fragment

open class BaseFragment: Fragment() {

    protected val viewModelCompositionRoot by lazy {
        (activity as BaseActivity).viewModelCompositionRoot
    }

    protected val analyticsReporter by lazy {
        (activity as BaseActivity).viewModelCompositionRoot.analyticsReporter
    }
}