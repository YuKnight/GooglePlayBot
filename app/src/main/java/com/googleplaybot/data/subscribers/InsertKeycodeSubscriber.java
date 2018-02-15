package com.googleplaybot.data.subscribers;

import android.content.Context;

import com.googleplaybot.utils.LogUtil;

public class InsertKeycodeSubscriber extends BaseKeycodeSubscriber<Long> {

    public InsertKeycodeSubscriber(Context context, int record, boolean sendTelegram) {
        super(context, record, sendTelegram);
    }

    @Override
    public void onCompleted() {}

    @Override
    public void onError(Throwable e) {
        LogUtil.e(getClass(), e.getMessage());
    }

    @Override
    public void onNext(Long object) {
        dbManager.selectKeycodes(record, null, null, false, sendTelegram);
    }
}