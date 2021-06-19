package com.aslansari.hypocoin.repository.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface AccountDAO {

    @Insert
    Completable addAccount(Account account);

    @Query("SELECT * FROM Account WHERE id = :id")
    Single<Account> getAccount(String id);

    @Query("UPDATE Account SET balance = :balance WHERE id = :id")
    Completable updateBalance(String id, long balance);

}
