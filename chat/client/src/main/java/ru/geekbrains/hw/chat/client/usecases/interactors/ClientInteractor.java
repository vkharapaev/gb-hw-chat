package ru.geekbrains.hw.chat.client.usecases.interactors;

import io.reactivex.Completable;
import ru.geekbrains.hw.chat.client.usecases.Client;
import ru.geekbrains.hw.chat.client.utils.MessageQueue;

public interface ClientInteractor {

    String MSG_END_AUTH = "//endauth";
    String MSG_END_CONNECTION = "//endconnection";

    void setClient(Client client);

    void start();

    Completable signIn(String login, String pass);

    MessageQueue getMessageQueue();

    MessageQueue getClientsMessageQueue();

    Completable sendMessage(String message);

    Completable register(String login, String nick, String pass);
}
