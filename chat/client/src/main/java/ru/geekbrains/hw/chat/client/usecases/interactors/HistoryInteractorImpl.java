package ru.geekbrains.hw.chat.client.usecases.interactors;

import ru.geekbrains.hw.chat.client.usecases.HistoryRepository;

import java.util.List;

public class HistoryInteractorImpl implements HistoryInteractor {

    private final HistoryRepository repository;

    public HistoryInteractorImpl(HistoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public void storeMessage(String message) {
        repository.storeMessage(message);
    }

    @Override
    public List<String> getHistory(int limit) {
        return repository.getHistory(limit);
    }

    @Override
    public void setSourceName(String name) {
        repository.setSourceName(name);
    }
}
