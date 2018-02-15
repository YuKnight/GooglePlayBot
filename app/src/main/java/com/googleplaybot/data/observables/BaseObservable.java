package com.googleplaybot.data.observables;

import com.squareup.sqlbrite.BriteDatabase;

import rx.Observable;

abstract class BaseObservable<T> implements Observable.OnSubscribe<T> {

    protected BriteDatabase db;

    public BaseObservable(BriteDatabase db) {
        this.db = db;
    }
}