package com.aslansari.hypocoin.repository.restapi

import com.google.gson.annotations.SerializedName

data class Data<T>(
    @SerializedName(value = "status")
    var status: Status,
    @SerializedName(value = "data")
    var data: T
)
