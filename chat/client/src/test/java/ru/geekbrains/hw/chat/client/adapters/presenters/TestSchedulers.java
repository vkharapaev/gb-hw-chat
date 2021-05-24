package ru.geekbrains.hw.chat.client.adapters.presenters;

import io.reactivex.Completable;
import ru.geekbrains.hw.chat.client.utils.Schedulers;

public final class TestSchedulers implements Schedulers {
    @Override
    public Completable subscribeOnIoObserveOnJavaFx(Completable completable) {
        return completable;
    }
}
