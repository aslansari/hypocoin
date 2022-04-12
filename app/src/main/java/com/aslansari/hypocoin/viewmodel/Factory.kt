package com.aslansari.hypocoin.viewmodel

interface Factory<T> {
    fun create(): T
}