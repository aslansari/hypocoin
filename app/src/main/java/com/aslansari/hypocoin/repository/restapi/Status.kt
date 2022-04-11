package com.aslansari.hypocoin.repository.restapi

import com.google.gson.annotations.SerializedName

class Status {
    @SerializedName(value = "elapsed")
    var elapsed = 0

    @SerializedName(value = "timestamp")
    var timestamp // TODO iso time stamp GSON converter
            : String? = null
}