package com.googleplaybot.events.remote.telegram;

import android.support.annotation.NonNull;

public class InboxEvent {

    @NonNull
    public String text;

    public long chatId;

    public long updateId;

    public long messageTime;

    public InboxEvent(@NonNull String text, long chatId, long updateId, long messageTime) {
        this.text = text;
        this.chatId = chatId;
        this.updateId = updateId;
        this.messageTime = messageTime;
    }
}
