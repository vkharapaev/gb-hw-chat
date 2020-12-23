package ru.geekbrains.hw.chat.client.adapters.data;

import java.util.List;

public interface HistoryDataSource {
    void storeMessage(String message);

    List<String> getHistory(int limit);

    void setSourceName(String name);
}
