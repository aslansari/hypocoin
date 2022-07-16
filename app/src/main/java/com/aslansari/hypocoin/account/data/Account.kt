package com.aslansari.hypocoin.account.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Account(
    @field:PrimaryKey val id: String,
    val email: String,
) {
    @ColumnInfo(name = "balance")
    var balance: Long = 0

    @Ignore var passwordPlaintext: String = ""
}