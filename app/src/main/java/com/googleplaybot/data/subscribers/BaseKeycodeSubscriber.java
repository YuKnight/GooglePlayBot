package com.googleplaybot.data.subscribers;

import android.content.Context;

import com.googleplaybot.data.DbManager;

abstract class BaseKeycodeSubscriber<T> extends BaseSubscriber<T> {

    protected DbManager dbManager;

    public BaseKeycodeSubscriber(Context context, int record, boolean sendTelegram) {
        super(record, sendTelegram);
        this.dbManager = new DbManager(context);
    }
}