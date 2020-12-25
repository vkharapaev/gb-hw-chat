package ru.geekbrains.hw.chat.client.usecases.interactors;

import ru.geekbrains.hw.chat.client.usecases.Client;

import java.util.concurrent.BlockingQueue;

public interface ClientInteractor {
    void setClient(Client client);

    void start();

    void signIn(String login, String pass);

    BlockingQueue<String> getMessageQueue();

    void sendMsg(String message);
}
