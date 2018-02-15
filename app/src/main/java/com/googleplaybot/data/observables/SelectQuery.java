package com.googleplaybot.data.observables;

import android.database.Cursor;

import com.googleplaybot.data.Db;
import com.googleplaybot.models.Keycode;

import rx.functions.Func1;

public class SelectQuery implements Func1<Cursor, Keycode> {

    @Override
    public Keycode call(Cursor cursor) {
        return Db.KeysTable.parseCursor(cursor);
    }
}