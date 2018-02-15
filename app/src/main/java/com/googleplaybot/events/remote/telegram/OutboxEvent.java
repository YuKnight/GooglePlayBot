package com.googleplaybot.events.remote.telegram;

import android.support.annotation.NonNull;

public class OutboxEvent {

    @NonNull
    public String text;

    public OutboxEvent(@NonNull String text) {
        this.text = text;
    }
}
