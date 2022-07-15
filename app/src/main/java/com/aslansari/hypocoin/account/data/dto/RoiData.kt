package com.aslansari.hypocoin.account.data.dto

import com.google.gson.annotations.SerializedName

data class RoiData(

    @SerializedName("percent_change_last_1_week")
    var percentChangeLast1Week: Double = 0.0
)
