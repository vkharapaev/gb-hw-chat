package ru.geekbrains.hw.chat.client.usecases;

import java.util.List;

public interface HistoryRepository {
    void storeMessage(String message);

    List<String> getHistory(int limit);

    void setSourceName(String name);
}
