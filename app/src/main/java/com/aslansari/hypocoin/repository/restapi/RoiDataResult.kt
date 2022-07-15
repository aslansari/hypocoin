package com.aslansari.hypocoin.repository.restapi

import com.aslansari.hypocoin.account.data.dto.RoiData
import com.google.gson.annotations.SerializedName

data class RoiDataResult(
    @SerializedName(value = "status")
    var status: Status,
    @SerializedName(value = "data")
    var data: Data
)

data class Data(
    @SerializedName("roi_data")
    val roiData: RoiData
)
