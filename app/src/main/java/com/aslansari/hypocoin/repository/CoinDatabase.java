package com.aslansari.hypocoin.repository;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.aslansari.hypocoin.repository.model.Account;
import com.aslansari.hypocoin.repository.model.AccountDAO;
import com.aslansari.hypocoin.repository.model.Currency;
import com.aslansari.hypocoin.repository.model.CurrencyDAO;

@Database(entities = {Currency.class, Account.class}, version = 1, exportSchema = false)
public abstract class CoinDatabase extends RoomDatabase {

    public static final String DB_NAME = "coin.db";

    public static CoinDatabase build(Context context) {
        Builder<CoinDatabase> databaseBuilder = Room.databaseBuilder(context, CoinDatabase.class, DB_NAME)
                .setJournalMode(JournalMode.TRUNCATE);
        return databaseBuilder.build();
    }

    public abstract CurrencyDAO currencyDAO();

    public abstract AccountDAO accountDAO();

}
