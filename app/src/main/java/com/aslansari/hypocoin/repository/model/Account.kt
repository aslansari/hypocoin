package com.aslansari.hypocoin.repository.model

import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity
data class Account(@field:PrimaryKey val id: String) {
    @ColumnInfo(name = "balance")
    var balance: Long = 0
}