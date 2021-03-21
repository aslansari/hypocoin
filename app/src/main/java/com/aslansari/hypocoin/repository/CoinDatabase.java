package com.aslansari.hypocoin.repository;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.aslansari.hypocoin.repository.model.Currency;
import com.aslansari.hypocoin.repository.model.CurrencyDAO;

@Database(entities = {Currency.class}, version = 1, exportSchema = false)
public abstract class CoinDatabase extends RoomDatabase {

    public static final String DB_NAME = "coin.db";
    private static CoinDatabase thisInstance;

    public abstract CurrencyDAO currencyDAO();

    public static void init(Context context) {
         Builder<CoinDatabase> databaseBuilder = Room.databaseBuilder(context, CoinDatabase.class, DB_NAME)
                .setJournalMode(JournalMode.TRUNCATE);
         thisInstance = databaseBuilder.build();
    }

    public static CoinDatabase getInstance() {
        return thisInstance;
    }

}
