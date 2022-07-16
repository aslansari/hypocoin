package com.aslansari.hypocoin.account.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface AccountDAO {
    @Insert
    fun addAccount(account: Account)

    @Query("SELECT * FROM Account WHERE id = :id")
    fun getAccount(id: String): Single<Account>

    @Query("UPDATE Account SET balance = :balance WHERE id = :id")
    fun updateBalance(id: String?, balance: Long): Completable
}