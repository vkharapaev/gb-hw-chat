package ru.geekbrains.hw.chat.client.utils;

import io.reactivex.Scheduler;

public interface Schedulers {
    Scheduler getIoScheduler();

    Scheduler getJavaFxScheduler();
}
