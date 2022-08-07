package com.aslansari.hypocoin.currency.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Currency(
    @JvmField
    @PrimaryKey
    var id: String,

    @JvmField
    @ColumnInfo(name = "symbol")
    @SerializedName("symbol")
    var symbol: String? = null,

    @ColumnInfo(name = "name")
    @SerializedName("name")
    var name: String? = null,

    @ColumnInfo(name = "slug")
    @SerializedName("slug")
    var slug: String? = null,

    @JvmField
    @Embedded
    @SerializedName("metrics")
    var metrics: Metrics? = null
) {
    constructor(): this("", null, null, null)
    constructor(id: String, symbol: String?, name: String?, slug: String?): this(id, symbol, name, slug, null)
}