package ru.geekbrains.hw.chat.server.adapters.data;

import ru.geekbrains.hw.chat.server.entities.User;

public interface DataSource {
    void start();

    User getUser(String login, String pass);

    User createUser(String login, String nick, String pass);

    boolean changeNick(long userId, String newNick);

    void stop();
}
