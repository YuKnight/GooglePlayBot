package com.googleplaybot.data.observables;

import android.database.sqlite.SQLiteDatabase;

import com.googleplaybot.data.Db;
import com.googleplaybot.models.Keycode;
import com.squareup.sqlbrite.BriteDatabase;

import rx.Subscriber;

public class InsertObservable extends BaseObservable<Long> {

    private Keycode keycode;

    public InsertObservable(BriteDatabase db, Keycode keycode) {
        super(db);
        this.keycode = keycode;
    }

    @Override
    public void call(Subscriber<? super Long> subscriber) {
        if (subscriber.isUnsubscribed()) {
            return;
        }
        BriteDatabase.Transaction transaction = db.newTransaction();
        long id = db.insert(Db.TABLE_KEYS, Db.KeysTable.toContentValues(keycode),
                SQLiteDatabase.CONFLICT_REPLACE);
        transaction.markSuccessful();
        transaction.end();
        subscriber.onNext(id);
    }
}