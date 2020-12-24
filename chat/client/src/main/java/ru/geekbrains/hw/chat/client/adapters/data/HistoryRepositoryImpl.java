package ru.geekbrains.hw.chat.client.adapters.data;

import ru.geekbrains.hw.chat.client.usecases.HistoryRepository;

import java.util.List;

public class HistoryRepositoryImpl implements HistoryRepository {

    private final HistoryDataSource dataSource;

    public HistoryRepositoryImpl(HistoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void storeMessage(String message) {
        dataSource.storeMessage(message);
    }

    @Override
    public List<String> getHistory(int limit) {
        return dataSource.getHistory(limit);
    }

    @Override
    public void setSourceName(String name) {
        dataSource.setSourceName(name);
    }
}
