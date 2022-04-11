package com.aslansari.hypocoin.repository.restapi

import com.google.gson.annotations.SerializedName

data class Asset<T>(
    @SerializedName(value = "status")
    var status: Status? = null,
    @SerializedName(value = "data")
    var assets: List<T>? = null
)
