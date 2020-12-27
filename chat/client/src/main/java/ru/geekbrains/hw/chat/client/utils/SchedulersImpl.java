package ru.geekbrains.hw.chat.client.utils;

import io.reactivex.Scheduler;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;

public class SchedulersImpl implements Schedulers {

    @Override
    public Scheduler getIoScheduler() {
        return io.reactivex.schedulers.Schedulers.io();
    }

    @Override
    public Scheduler getJavaFxScheduler() {
        return JavaFxScheduler.platform();
    }
}
