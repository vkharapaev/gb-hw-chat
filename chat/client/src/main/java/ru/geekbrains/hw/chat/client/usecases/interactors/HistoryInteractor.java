package ru.geekbrains.hw.chat.client.usecases.interactors;

import java.util.List;

public interface HistoryInteractor {
    void storeMessage(String message);

    List<String> getHistory(int limit);

    void setSourceName(String name);
}
