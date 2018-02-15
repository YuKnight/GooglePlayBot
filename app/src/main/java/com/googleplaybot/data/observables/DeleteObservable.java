package com.googleplaybot.data.observables;

import com.squareup.sqlbrite.BriteDatabase;

import rx.Subscriber;

public class DeleteObservable extends BaseObservable<Void> {

    private String query;

    public DeleteObservable(BriteDatabase db, String query) {
        super(db);
        this.query = query;
    }

    @Override
    public void call(Subscriber<? super Void> subscriber) {
        if (subscriber.isUnsubscribed()) {
            return;
        }
        BriteDatabase.Transaction transaction = db.newTransaction();
        db.execute(query);
        transaction.markSuccessful();
        transaction.end();
        subscriber.onCompleted();
    }
}