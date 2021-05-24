package ru.geekbrains.hw.chat.client.utils;

import javafx.application.Platform;

public class JavaFxImpl implements JavaFx {

    @Override
    public void runLater(Runnable runnable) {
        Platform.runLater(runnable);
    }
}
