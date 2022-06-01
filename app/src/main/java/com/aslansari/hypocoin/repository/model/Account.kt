package com.aslansari.hypocoin.repository.model

import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore

@Entity
data class Account(
    @field:PrimaryKey val id: String,
    val email: String,
) {
    @ColumnInfo(name = "balance")
    var balance: Long = 0

    @Ignore var passwordPlaintext: String = ""
}