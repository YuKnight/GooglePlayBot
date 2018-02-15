package com.googleplaybot.events.ui;

import android.support.annotation.Nullable;

public class LogEvent {

    @Nullable
    public CharSequence log;

    public LogEvent(@Nullable CharSequence log) {
        this.log = log;
    }
}
