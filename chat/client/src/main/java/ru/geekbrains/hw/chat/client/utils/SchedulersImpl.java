package ru.geekbrains.hw.chat.client.utils;

import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;

public class SchedulersImpl implements Schedulers {

    private Scheduler getIoScheduler() {
        return io.reactivex.schedulers.Schedulers.io();
    }

    private Scheduler getJavaFxScheduler() {
        return JavaFxScheduler.platform();
    }

    @Override
    public Completable subscribeOnIoObserveOnJavaFx(Completable completable) {
        return completable
                .subscribeOn(getIoScheduler())
                .observeOn(getJavaFxScheduler());
    }
}
