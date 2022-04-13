package com.aslansari.hypocoin.ui

import androidx.fragment.app.Fragment

open class BaseFragment: Fragment() {

    protected val viewModelCompositionRoot by lazy {
        (activity as BaseActivity).viewModelCompositionRoot
    }
}