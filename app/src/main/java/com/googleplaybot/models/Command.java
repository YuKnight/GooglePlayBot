package com.googleplaybot.models;

import android.support.annotation.Nullable;

import com.googleplaybot.utils.LogUtil;

public class Command {

	public String user;

	public String text;

    public Command(String user, String text) {
        this.user = user;
        this.text = text;
    }

    @Nullable
	public String getAPI() {
        if (!isValidCommand()) {
            return null;
        }
        String[] parts = text.trim().split(" ");
        return parts[0].trim().toLowerCase();
    }

    @Nullable
    public String getFirstParameter() {
        if (!isValidCommand()) {
            return null;
        }
        String[] parts = text.trim().split(" ");
        return parts.length > 1 ? parts[1].trim() : null;
    }

    @Nullable
    public String getSecondParameter() {
        if (!isValidCommand()) {
            return null;
        }
        String[] parts = text.trim().split(" ");
        return parts.length > 2 ? parts[2].trim() : null;
    }

    private boolean isValidCommand() {
        if (text == null) {
            LogUtil.w(getClass(), "Null command text");
            return false;
        }
        if (text.trim().isEmpty()) {
            LogUtil.w(getClass(), "Empty command text");
            return false;
        }
        return true;
    }
}