package com.googleplaybot.models.remote;

import com.google.gson.annotations.SerializedName;

public class Download {

    @SerializedName("id")
    public long id;

    public transient long device;

    @SerializedName("task")
    public long task;
    @SerializedName("account")
    public long account;

    public transient String filename;

    public Download() {}
}