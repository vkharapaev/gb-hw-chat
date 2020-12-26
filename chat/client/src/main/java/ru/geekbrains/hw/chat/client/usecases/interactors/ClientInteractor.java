package ru.geekbrains.hw.chat.client.usecases.interactors;

import io.reactivex.Completable;
import ru.geekbrains.hw.chat.client.usecases.Client;

import java.util.concurrent.BlockingQueue;

public interface ClientInteractor {

    String MSG_END_AUTH = "//endauth";
    String MSG_END_CHAT = "//endchat";

    void setClient(Client client);

    void start();

    Completable signIn(String login, String pass);

    BlockingQueue<String> getMessageQueue();

    BlockingQueue<String> getClientsMessageQueue();

    Completable sendMessage(String message);

    Completable register(String login, String nick, String pass);
}
