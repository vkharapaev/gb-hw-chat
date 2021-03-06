package ru.geekbrains.hw.chat.server.usecases;

import ru.geekbrains.hw.chat.server.entities.User;

public interface Repository {
    void start();

    User getUser(String login, String pass);

    User createUser(String login, String nick, String pass);

    boolean changeNick(long userId, String newNick);

    void stop();
}
