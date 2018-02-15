package com.googleplaybot.events.services;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.googleplaybot.models.Keycode;

import java.util.ArrayList;
import java.util.List;

public class KeycodesEvent {

    public int record;

    @NonNull
    public ArrayList<Keycode> keycodes;

    public String firstParameter;

    @Nullable
    public String secondParameter;

    public KeycodesEvent(int record, @NonNull List<Keycode> keycodes, String firstParameter,
                         @Nullable String secondParameter) {
        this.record = record;
        this.keycodes = new ArrayList<>();
        for (int i = 0 ; i < keycodes.size(); i++) {
            if (keycodes.get(i).record == record) {
                this.keycodes.add(keycodes.get(i));
            }
        }
        this.firstParameter = firstParameter;
        this.secondParameter = secondParameter;
    }
}
