package com.googleplaybot.events.services;

import android.support.annotation.Nullable;

import com.googleplaybot.models.Command;

public class PlayEvent {

    public int record;

    public String firstParameter;

    @Nullable
    public String secondParameter;

    public PlayEvent(int record, Command command) {
        this.record = record;
        this.firstParameter = command.getFirstParameter();
        this.secondParameter = command.getSecondParameter();
    }
}
