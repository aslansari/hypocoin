package com.aslansari.hypocoin.viewmodel

class Resource<T> private constructor(
    val status: DataStatus,
    val value: T?,
    val throwable: Throwable?
) {
    val isLoading: Boolean
        get() = status === DataStatus.LOADING

    companion object {
        @JvmStatic
        fun <T> error(t: T, throwable: Throwable?): Resource<T> {
            return Resource(DataStatus.ERROR, t, throwable)
        }

        @JvmStatic
        fun <T> loading(t: T?): Resource<T> {
            return Resource(DataStatus.LOADING, t, null)
        }

        @JvmStatic
        fun <T> complete(t: T): Resource<T> {
            return Resource(DataStatus.COMPLETE, t, null)
        }
    }
}