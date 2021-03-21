package com.aslansari.hypocoin.repository.restapi;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Asset<T> {

    @SerializedName(value = "status")
    public Status status;
    @SerializedName(value = "data")
    public List<T> assets;

}
