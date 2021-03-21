package com.aslansari.hypocoin.repository.restapi;

import com.google.gson.annotations.SerializedName;

public final class Status {
    @SerializedName(value = "elapsed")
    public int elapsed;
    @SerializedName(value = "timestamp")
    public String timestamp; // TODO iso time stamp GSON converter
}
