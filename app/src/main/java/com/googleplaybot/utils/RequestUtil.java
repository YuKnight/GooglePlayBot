package com.googleplaybot.utils;

import android.support.annotation.Nullable;

import com.googleplaybot.events.remote.telegram.InboxEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class RequestUtil {

    @Nullable
    public static String getResponse(Response response) throws IOException {
        if (!response.isSuccessful()) {
            LogUtil.w(RequestUtil.class.getClass(), "Response is unsuccessful");
            return null;
        }
        ResponseBody body = response.body();
        if (body == null) {
            LogUtil.w(RequestUtil.class.getClass(), "Response body is null");
            return null;
        }
        String string = body.string().trim();
        if (string.isEmpty()) {
            LogUtil.w(RequestUtil.class.getClass(), "Response is empty...");
            return null;
        }
        return string;
    }

    public static Request createGetRequest(String url, String tag) {
        return new Request.Builder()
                .url(url)
                .tag(tag)
                .build();
    }

    public static Request createPostRequest(String url, String tag, HashMap<String, String> params) {
        FormBody.Builder formBody = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            formBody.add(entry.getKey(), entry.getValue());
        }
        return new Request.Builder()
                .url(url)
                .tag(tag)
                .post(formBody.build())
                .build();
    }

    @Nullable
    public static InboxEvent handleInboxResponse(String text) {
        try {
            JSONObject response = new JSONObject(text);
            if (!response.getBoolean("ok")) {
                LogUtil.w(RequestUtil.class.getClass(), "Unsuccessful telegram response");
                return null;
            }
            JSONArray result = response.getJSONArray("result");
            if (result.length() > 0) {
                JSONObject firstElement = result.getJSONObject(0);
                JSONObject message = firstElement.getJSONObject("message");
                JSONObject chat = message.getJSONObject("chat");
                return new InboxEvent(message.getString("text"), chat.getLong("id"),
                        firstElement.getLong("update_id"), message.getLong("date"));
            }
        } catch (JSONException e) {
            LogUtil.e(RequestUtil.class.getClass(), e.getMessage());
        }
        return null;
    }
}
