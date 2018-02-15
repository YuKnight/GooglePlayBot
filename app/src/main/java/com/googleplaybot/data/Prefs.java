package com.googleplaybot.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.googleplaybot.utils.DeviceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import timber.log.Timber;

public class Prefs {

    public static final String DEFAULT_TELEGRAM_USERNAME = "TelegramBot";
    public static final String DEFAULT_TELEGRAM_BOT_TOKEN =
            "378576431:AAG4QFlFNVXzdoOJgT92stvOaS42SohZopM";

    public static final int MODE_DEBUG = 1;
    public static final int MODE_RELEASE = 2;

    /* Preferences */

    public static final String DEVICE_MODE = "deviceMode";
    public static final String DEVICE_NAME = "deviceName";

    public static final String TELEGRAM_BOT_TOKEN = "TelegramBotToken";
    public static final String TELEGRAM_UPDATE_ID = "TelegramUpdateId";
    public static final String TELEGRAM_CHAT_ID = "TelegramChatId";

    public static final String CONSOLE_IS_ACTIVE = "consoleIsActive";

    /* Util strings */

    private static final String EMPTY = "";

    /* SharedPreferences parameters */

    private SharedPreferences preferences;

    public Prefs(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @SuppressWarnings("unused")
    public int getDeviceMode() {
        return preferences.getInt(DEVICE_MODE, MODE_DEBUG);
    }

    @SuppressWarnings("unused")
    public String getDeviceName() {
        return preferences.getString(DEVICE_NAME, DeviceUtil.getDeviceModel()
                .replaceAll(" ", "")).trim();
    }

    @SuppressWarnings("unused")
    public String getTelegramBotToken() {
        return preferences.getString(TELEGRAM_BOT_TOKEN, DEFAULT_TELEGRAM_BOT_TOKEN).trim();
    }

    @SuppressWarnings("unused")
    public String getString(String name) {
        return preferences.getString(name, EMPTY).trim();
    }

    @SuppressWarnings("all")
    public <T> String getString(String name, T def) {
        return preferences.getString(name, toString(def)).trim();
    }

    @SuppressWarnings("unused")
    public boolean getBoolean(String name) {
        return preferences.getBoolean(name, false);
    }

    @SuppressWarnings("unused")
    public int getInt(String name) {
        return preferences.getInt(name, 0);
    }

    @SuppressWarnings("unused")
    public long getLong(String name) {
        return preferences.getLong(name, 0L);
    }

    @SuppressWarnings("unused")
    public <T> void putString(String name, T value) {
        preferences.edit().putString(name, toString(value)).apply();
    }

    @SuppressWarnings("unused")
    public void putBoolean(String name, boolean value) {
        preferences.edit().putBoolean(name, value).apply();
    }

    @SuppressWarnings("unused")
    public void putInt(String name, int value) {
        preferences.edit().putInt(name, value).apply();
    }

    @SuppressWarnings("unused")
    public void putLong(String name, long value) {
        preferences.edit().putLong(name, value).apply();
    }

    /* Controls functions */

    @SuppressWarnings("unused")
    public boolean has(String name) {
        return preferences.contains(name);
    }

    @SuppressWarnings("unused")
    public void clear() {
        preferences.edit().clear().apply();
    }

    @SuppressWarnings("unused")
    public void remove(String name) {
        if (has(name)) {
            preferences.edit().remove(name).apply();
        }
    }

    /* Utils functions */

    @SuppressWarnings("unused")
    private  <T> String toString(T value) {
        return String.class.isInstance(value)? ((String) value).trim() : String.valueOf(value);
    }

    @SuppressWarnings("unused")
    public void printAll() {
        Map<String, ?> preferencesAll = preferences.getAll();
        if (preferencesAll == null) {
            return;
        }
        ArrayList<Map.Entry<String, ?>> list = new ArrayList<>();
        list.addAll(preferencesAll.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, ?>>() {
            public int compare(final Map.Entry<String, ?> entry1, final Map.Entry<String, ?> entry2) {
                return entry1.getKey().compareTo(entry2.getKey());
            }
        });
        Timber.d("Printing all sharedPreferences");
        for(Map.Entry<String, ?> entry : list) {
            Timber.d(entry.getKey() + ": " + entry.getValue());
        }
    }
}
