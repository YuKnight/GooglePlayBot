package com.googleplaybot.remote;

import android.support.annotation.Nullable;

import com.googleplaybot.events.remote.ResponseEvent;
import com.googleplaybot.utils.LogUtil;
import com.googleplaybot.utils.EventUtil;
import com.googleplaybot.utils.RequestUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class RequestBase implements Callback {

    public static final String TAG_GET_TELEGRAM_MESSAGES = "GET_TELEGRAM_MESSAGES";
    private static final String TAG_NONE = "NONE";

    private static final OkHttpClient client;

    static {
        client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public RequestBase() {}

    public void asyncGet(String url, @Nullable String tag) {
        if (tag == null) {
            tag = TAG_NONE;
        }
        client.newCall(RequestUtil.createGetRequest(url, tag))
                .enqueue(this);
    }

    public void asyncPost(String url, @Nullable String tag, HashMap<String, String> params) {
        if (tag == null) {
            tag = TAG_NONE;
        }
        client.newCall(RequestUtil.createPostRequest(url, tag, params))
                .enqueue(this);
    }

    @Override
    public void onFailure(Call call, IOException e) {
        LogUtil.e(getClass(), e.getMessage());
        EventUtil.post(new ResponseEvent(false));
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        String text = RequestUtil.getResponse(response);
        if (text == null) {
            EventUtil.post(new ResponseEvent(false));
            return;
        }
        EventUtil.post(new ResponseEvent(true));
        switch ((String) call.request().tag()) {
            case TAG_GET_TELEGRAM_MESSAGES:
                EventUtil.post(RequestUtil.handleInboxResponse(text));
                break;
            default:
                break;
        }
    }
}
