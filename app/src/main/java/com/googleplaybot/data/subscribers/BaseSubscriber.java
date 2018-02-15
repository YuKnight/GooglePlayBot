package com.googleplaybot.data.subscribers;

import rx.Subscriber;

abstract class BaseSubscriber<T> extends Subscriber<T> {

    protected int record;

    protected boolean sendTelegram;

    public BaseSubscriber(int record, boolean sendTelegram) {
        this.record = record;
        this.sendTelegram = sendTelegram;
    }
}