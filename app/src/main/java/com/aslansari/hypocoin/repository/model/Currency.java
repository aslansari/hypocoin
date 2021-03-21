package com.aslansari.hypocoin.repository.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class Currency {
    @PrimaryKey
    @NonNull
    public String id;
    @ColumnInfo(name = "symbol")
    @SerializedName("symbol")
    public String symbol;
    @ColumnInfo(name = "name")
    @SerializedName("name")
    public String name;

    @ColumnInfo(name = "slug")
    @SerializedName("slug")
    public String slug;

    @Ignore // TODO implement database mapping (e.g. @Embedded)
    @SerializedName("metrics")
    public Metrics metrics;

}
