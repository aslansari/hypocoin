package com.aslansari.hypocoin.account.data

import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.aslansari.hypocoin.account.data.dto.AssetItemDTO
import com.aslansari.hypocoin.currency.data.CurrencyRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import timber.log.Timber

class AssetRepository(
    private val accountRepository: AccountRepository,
    private val currencyRepository: CurrencyRepository,
    private val database: DatabaseReference,
) {

    private val usersReference = database.child(DatabaseModel.USERS)
    private var assetEventListener: ValueEventListener? = null

    private fun assetsFlow(uid: String): Flow<List<AssetItemDTO>> = callbackFlow {
        assetEventListener = usersReference.child(uid).child(DatabaseModel.ASSETS).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val assetItemDTOs = snapshot.children.map { it.getValue(AssetItemDTO::class.java) as AssetItemDTO}.toList()
                trySend(assetItemDTOs)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.d("asset read error ${error.code}")
                cancel(CancellationException("asset read error"))
            }
        })
        awaitClose {
            assetEventListener?.let {
                usersReference.child(uid).child(DatabaseModel.ASSETS).removeEventListener(it)
            }
        }
    }.shareIn(
        ProcessLifecycleOwner.get().lifecycleScope,
        SharingStarted.WhileSubscribed(),
        1
    )

    private fun assetsFlowWithUser() = accountRepository.getAccountFlow().flatMapConcat { result ->
        when (result) {
            is UserResult.User -> assetsFlow(result.uid)
            else -> flow { emptyList<List<AssetItemDTO>>() }
        }
    }

    fun assetItems() = assetsFlowWithUser().map { list ->
        val assets = list.map {
            val (id, symbol, name, amount) = it
            val roiData = currencyRepository.getRoi(id)
            AssetItem(id, symbol, name, amount, roiData)
        }
        assets
    }

    val assetListState = assetItems().stateIn(
        ProcessLifecycleOwner.get().lifecycleScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )
}