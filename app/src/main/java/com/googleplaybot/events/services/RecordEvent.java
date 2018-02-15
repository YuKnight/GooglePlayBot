package com.googleplaybot.events.services;

import android.support.annotation.NonNull;

import com.googleplaybot.models.Keycode;

public class RecordEvent {

    public static final int TYPE_INSERT_KEYCODE = 1;
    public static final int TYPE_DELETE_KEYCODE = 2;
    public static final int TYPE_PRINT = 3;
    public static final int TYPE_CLEAR = 4;

    public int type;

    @NonNull
    public Keycode keycode;

    public RecordEvent(int type, @NonNull Keycode keycode) {
        this.type = type;
        this.keycode = keycode;
    }
}
