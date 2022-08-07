package com.aslansari.hypocoin.di

import android.content.Context
import com.aslansari.hypocoin.account.data.AccountDAO
import com.aslansari.hypocoin.currency.data.model.CurrencyDAO
import com.aslansari.hypocoin.repository.CoinDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class CurrencyDatabaseModule() {

    @Provides
    fun database(@ApplicationContext context: Context): CoinDatabase {
        return CoinDatabase.getDatabase(context)
    }

    @Provides
    fun accountDao(coinDatabase: CoinDatabase): AccountDAO {
        return coinDatabase.accountDAO()
    }

    @Provides
    fun currencyDao(coinDatabase: CoinDatabase): CurrencyDAO {
        return coinDatabase.currencyDAO()
    }
}