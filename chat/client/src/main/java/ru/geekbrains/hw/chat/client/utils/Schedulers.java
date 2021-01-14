package ru.geekbrains.hw.chat.client.utils;

import io.reactivex.Completable;

public interface Schedulers {
    Completable subscribeOnIoObserveOnJavaFx(Completable completable);
}
