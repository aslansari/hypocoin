package com.aslansari.hypocoin.account.data

import com.aslansari.hypocoin.currency.data.RoiData
import kotlin.random.Random

class AssetRepository {

    fun getAssets(uid: String): List<AssetItem> {
        // todo fetch from api
        return listOf(
            AssetItem("1e31218a-e44e-4285-820c-8282ee222035", "BTC", "bitcoin", 0.00000012, getFakeRoiData()),
            AssetItem("21c795f5-1bfd-40c3-858e-e9d7e820c6d0", "ETH", "etherium", 0.00000012, getFakeRoiData()),
            AssetItem("7d793fa7-5fc6-432a-b26b-d1b10769d42e", "DOGE", "dogecoin", 0.00002012, getFakeRoiData()),
        )
    }

    private fun getFakeRoiData(): RoiData {
        return RoiData(Random.nextDouble(-1.0,1.0))
    }
}