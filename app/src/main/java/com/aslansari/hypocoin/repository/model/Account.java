package com.aslansari.hypocoin.repository.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity
public class Account {
    @PrimaryKey
    @NonNull
    public final String id;

    @ColumnInfo(name = "balance")
    private long balance;

    public Account(@NotNull String id) {
        this.id = id;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }
}
