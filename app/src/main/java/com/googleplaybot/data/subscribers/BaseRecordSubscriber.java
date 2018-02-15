package com.googleplaybot.data.subscribers;

abstract class BaseRecordSubscriber<T> extends BaseSubscriber<T> {

    public BaseRecordSubscriber(int record, boolean sendTelegram) {
        super(record, sendTelegram);
    }
}