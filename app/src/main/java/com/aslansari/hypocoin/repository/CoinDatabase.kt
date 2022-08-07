package com.aslansari.hypocoin.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aslansari.hypocoin.account.data.Account
import com.aslansari.hypocoin.account.data.AccountDAO
import com.aslansari.hypocoin.currency.data.model.Currency
import com.aslansari.hypocoin.currency.data.model.CurrencyDAO

@Database(entities = [Currency::class, Account::class], version = 1)
abstract class CoinDatabase : RoomDatabase() {
    abstract fun currencyDAO(): CurrencyDAO
    abstract fun accountDAO(): AccountDAO

    companion object {
        @Volatile
        private var INSTANCE: CoinDatabase? = null
        fun getDatabase(context: Context): CoinDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CoinDatabase::class.java,
                    "coin.db").build()
                INSTANCE = instance
                instance
            }
        }
    }
}