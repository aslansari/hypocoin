package com.aslansari.hypocoin.ui

import androidx.fragment.app.DialogFragment

open class BaseDialogFragment: DialogFragment() {

    protected val viewModelCompositionRoot by lazy {
        (activity as BaseActivity).viewModelCompositionRoot
    }
}