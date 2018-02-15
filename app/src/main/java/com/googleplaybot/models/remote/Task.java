package com.googleplaybot.models.remote;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Task {

    @SerializedName("id")
    public long id;
    @SerializedName("package")
    public String pckg;
    @SerializedName("downloads")
    public long downloads;
    @SerializedName("successes")
    public long successes;
    @SerializedName("time")
    public Date time;

    public Task() {}
}