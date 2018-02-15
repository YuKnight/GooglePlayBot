package com.googleplaybot.data.subscribers;

import android.content.Context;

import com.googleplaybot.utils.LogUtil;

public class DeleteKeycodeSubscriber extends BaseKeycodeSubscriber<Void> {

    public DeleteKeycodeSubscriber(Context context, int record, boolean sendTelegram) {
        super(context, record, sendTelegram);
    }

    @Override
    public void onCompleted() {
        dbManager.selectKeycodes(record, null, null, false, sendTelegram);
    }

    @Override
    public void onError(Throwable e) {
        LogUtil.e(getClass(), e.getMessage());
    }

    @Override
    public void onNext(Void nothing) {}
}