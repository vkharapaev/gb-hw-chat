package ru.geekbrains.hw.chat.server.usecases.interactors;

import ru.geekbrains.hw.chat.server.usecases.Repository;
import ru.geekbrains.hw.chat.server.usecases.Server;

public interface ServerInteractor {
    boolean isNickBusy(String nick);

    Repository getUserRepository();

    void broadcast(String message);

    boolean subscribe(ClientHandlerInteractor handler, String nick);

    void sendMsgToClient(ClientHandlerInteractor from, String toNick, String message);

    void broadcastClientsList();

    void unsubscribe(ClientHandlerInteractor handler);

    void start();

    void setServer(Server server);
}
