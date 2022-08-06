package com.aslansari.hypocoin.repository.restapi

import com.aslansari.hypocoin.account.data.dto.RoiData
import com.google.gson.annotations.SerializedName

data class RoiResult(
    @SerializedName("roi_data")
    val roiData: RoiData
)
