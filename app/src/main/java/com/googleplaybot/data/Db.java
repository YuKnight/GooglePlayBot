package com.googleplaybot.data;

import android.content.ContentValues;
import android.database.Cursor;

import com.googleplaybot.models.Keycode;

public class Db {

    public static final String DATABASE_NAME = "app.sqlite";

    public static final String TABLE_KEYS = "keys";

    public static final String COLUMN_ID = "id";

    public static final int CODE_PASSWORD = -2;
    public static final int CODE_GMAIL = -1;

    public static final int CODE_NONE = 0;

    public static final int CODE_UP = 19;
    public static final int CODE_DOWN = 20;
    public static final int CODE_LEFT = 21;
    public static final int CODE_RIGHT = 22;
    public static final int CODE_POWER = 26;
    public static final int CODE_TAB = 61;
    public static final int CODE_ENTER = 66;
    public static final int CODE_MENU = 82;

    public static final int RECORD_NONE = 0;
    public static final int RECORD_GOOGLE_PLAY_INSTALLATION = 1;
    public static final int RECORD_GOOGLE_ACCOUNT_SIGN_IN = 2;

    public static class KeysTable {

        public static final String COLUMN_CODE = "code";
        public static final String COLUMN_RECORD = "record";
        public static final String COLUMN_PACKAGE = "package";
        public static final String COLUMN_ACTIVITY = "activity";

        public static ContentValues toContentValues(Keycode item) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_RECORD, item.record);
            values.put(COLUMN_CODE, item.code);
            values.put(COLUMN_PACKAGE, item.pckg);
            values.put(COLUMN_ACTIVITY, item.activity);
            return values;
        }

        public static Keycode parseCursor(Cursor cursor) {
            Keycode item = new Keycode();
            item.id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
            item.record = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECORD));
            item.code = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CODE));
            item.pckg = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PACKAGE));
            item.activity = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACTIVITY));
            return item;
        }
    }
}
