package com.googleplaybot.data;

import android.content.Context;
import android.support.annotation.Nullable;

import com.googleplaybot.data.observables.DeleteObservable;
import com.googleplaybot.data.observables.InsertObservable;
import com.googleplaybot.data.observables.SelectQuery;
import com.googleplaybot.data.subscribers.DeleteKeycodeSubscriber;
import com.googleplaybot.data.subscribers.DeleteRecordSubscriber;
import com.googleplaybot.data.subscribers.InsertKeycodeSubscriber;
import com.googleplaybot.data.subscribers.SelectRecordSubscriber;
import com.googleplaybot.models.Keycode;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import rx.Observable;
import rx.schedulers.Schedulers;

public class DbManager {

    private BriteDatabase db;

    public DbManager(Context context) {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        dbOpenHelper.openDatabase(context);
        db = new SqlBrite.Builder()
                .build()
                .wrapDatabaseHelper(dbOpenHelper, Schedulers.io());
    }

    public void selectKeycodes(int record, String firstParameter, @Nullable String secondParameter,
                               boolean postEvent, boolean sendTelegram) {
        db.createQuery(Db.TABLE_KEYS, "SELECT * FROM " + Db.TABLE_KEYS)
                .mapToList(new SelectQuery())
                .take(1)
                .subscribe(new SelectRecordSubscriber(record, firstParameter, secondParameter,
                        postEvent, sendTelegram));
    }

    public void insertKeycode(Context context, Keycode keycode, boolean sendTelegram) {
        Observable.create(new InsertObservable(db, keycode))
                .take(1)
                .subscribe(new InsertKeycodeSubscriber(context, keycode.record, sendTelegram));
    }

    public void deleteLastKeycode(Context context, int record, boolean sendTelegram) {
        Observable.create(new DeleteObservable(db, "DELETE FROM " + Db.TABLE_KEYS + " WHERE " +
                Db.COLUMN_ID + " = (SELECT MAX(" + Db.COLUMN_ID + ") FROM " + Db.TABLE_KEYS + ")" +
                " AND " + Db.KeysTable.COLUMN_RECORD + " = " + record))
                .take(1)
                .subscribe(new DeleteKeycodeSubscriber(context, record, sendTelegram));
    }

    public void deleteRecord(int record, boolean sendTelegram) {
        Observable.create(new DeleteObservable(db, "DELETE FROM " + Db.TABLE_KEYS + " WHERE " +
                Db.KeysTable.COLUMN_RECORD + " = " + record))
                .take(1)
                .subscribe(new DeleteRecordSubscriber(record, sendTelegram));
    }
}
