package com.googleplaybot.remote.server;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.googleplaybot.models.remote.GoogleAccount;
import com.googleplaybot.models.remote.Task;
import com.googleplaybot.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class JSONResponseParser {

    private JSONArray array;

    public JSONResponseParser(String json) {
        try {
            array = new JSONArray(json);

        } catch (JSONException e) {
            LogUtil.e(getClass(), e.getMessage());
        }
    }

    /*@NonNull
    public List<Task> getTasks() {

    }

    @Nullable
    public Task getActiveTask() {

    }

    @NonNull
    public List<GoogleAccount> getUnusedAccounts() {

    }*/
}