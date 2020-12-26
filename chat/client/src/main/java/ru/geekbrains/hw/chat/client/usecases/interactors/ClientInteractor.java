package ru.geekbrains.hw.chat.client.usecases.interactors;

import ru.geekbrains.hw.chat.client.usecases.Client;

import java.util.concurrent.BlockingQueue;

public interface ClientInteractor {

    String MSG_END_AUTH = "//endauth";
    String MSG_END_CHAT = "//endchat";

    void setClient(Client client);

    void start();

    void signIn(String login, String pass);

    BlockingQueue<String> getMessageQueue();

    BlockingQueue<String> getClientsMessageQueue();

    void sendMsg(String message);
}
