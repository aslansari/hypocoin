package com.aslansari.hypocoin.viewmodel.login

class LoginUIModel(val isLoading: Boolean, val isFailed: Boolean, val isComplete: Boolean) {
    companion object {
        fun idle(): LoginUIModel {
            return LoginUIModel(false, false, false)
        }

        fun loading(): LoginUIModel {
            return LoginUIModel(true, false, false)
        }

        fun fail(): LoginUIModel {
            return LoginUIModel(false, true, false)
        }

        @JvmStatic
        fun complete(): LoginUIModel {
            return LoginUIModel(false, false, true)
        }
    }
}