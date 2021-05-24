package ru.geekbrains.hw.chat.server.adapters.data;

import ru.geekbrains.hw.chat.server.entities.User;
import ru.geekbrains.hw.chat.server.usecases.Repository;

public class RepositoryImpl implements Repository {

    private final DataSource dataSource;

    public RepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void start() {
        dataSource.start();
    }

    @Override
    public User getUser(String login, String pass) {
        return dataSource.getUser(login, pass);
    }

    @Override
    public User createUser(String login, String nick, String pass) {
        return dataSource.createUser(login, nick, pass);
    }

    @Override
    public boolean changeNick(long userId, String newNick) {
        return dataSource.changeNick(userId, newNick);
    }

    @Override
    public void stop() {
        dataSource.stop();
    }

}
