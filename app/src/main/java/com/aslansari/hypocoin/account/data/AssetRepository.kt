package com.aslansari.hypocoin.account.data

import com.aslansari.hypocoin.account.data.dto.AssetItemDTO
import com.aslansari.hypocoin.currency.data.CurrencyRepository
import com.aslansari.hypocoin.currency.data.RoiData
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.random.Random

class AssetRepository(
    private val currencyRepository: CurrencyRepository,
    private val database: DatabaseReference,
) {

    private val usersReference = database.child(DatabaseModel.USERS)

    private val btcAmount by lazy { Random.nextDouble(1.0) }
    private val ethAmount by lazy { Random.nextDouble(1.0) }
    private val dogeAmount by lazy { Random.nextDouble(1.0) }
    private val btcRoiRate by lazy { getFakeRoiData() }
    private val ethRoiRate by lazy { getFakeRoiData() }
    private val dogeRoiRate by lazy { getFakeRoiData() }

    private suspend fun getAssetDTOList(uid: String): List<AssetItemDTO> = suspendCoroutine { continuation ->
        usersReference.child(uid).child(DatabaseModel.ASSETS).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val assetDTO = task.result.children.map { it.getValue(AssetItemDTO::class.java) as AssetItemDTO}.toList()
                continuation.resume(assetDTO)
            } else {
                continuation.resume(emptyList())
            }
        }
    }

    suspend fun getAssetList(uid: String): List<AssetItem> = withContext(Dispatchers.IO) {
        val assetItemDTOs = getAssetDTOList(uid)
        val assets = assetItemDTOs.map {
            val (id, symbol, name, amount) = it
            val roiData = currencyRepository.getRoi(id)
            AssetItem(id, symbol, name, amount, roiData)
        }
        assets
    }

    fun getAssets(uid: String): List<AssetItem> {
        // todo fetch from api
        return listOf(
            AssetItem(
                "1e31218a-e44e-4285-820c-8282ee222035",
                "BTC",
                "bitcoin",
                btcAmount,
                btcRoiRate,
            ),
            AssetItem(
                "21c795f5-1bfd-40c3-858e-e9d7e820c6d0",
                "ETH",
                "etherium",
                ethAmount,
                ethRoiRate
            ),
            AssetItem(
                "7d793fa7-5fc6-432a-b26b-d1b10769d42e",
                "DOGE",
                "dogecoin",
                dogeAmount,
                dogeRoiRate
            ),
        )
    }

    private fun getFakeRoiData(): RoiData {
        return RoiData(Random.nextDouble(-1.0,1.0))
    }
}