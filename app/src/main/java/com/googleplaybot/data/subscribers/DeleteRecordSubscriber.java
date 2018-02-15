package com.googleplaybot.data.subscribers;

import com.googleplaybot.events.remote.telegram.OutboxEvent;
import com.googleplaybot.models.Keycode;
import com.googleplaybot.utils.EventUtil;
import com.googleplaybot.utils.LogUtil;

public class DeleteRecordSubscriber extends BaseRecordSubscriber<Void> {

    public DeleteRecordSubscriber(int record, boolean sendTelegram) {
        super(record, sendTelegram);
    }

    @Override
    public void onCompleted() {
        String description = "Successfully cleared the " + Keycode.record2String(record);
        LogUtil.print(description);
        if (sendTelegram) {
            EventUtil.post(new OutboxEvent(description));
        }
    }

    @Override
    public void onError(Throwable e) {
        LogUtil.e(getClass(), e.getMessage());
    }

    @Override
    public void onNext(Void nothing) {}
}