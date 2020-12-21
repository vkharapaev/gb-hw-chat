package ru.geekbrains.hw.chat.server.usecases.interactors;

import ru.geekbrains.hw.chat.server.usecases.ClientHandler;

public interface ClientHandlerInteractor {
    String getName();

    void onStart();

    void setClientHandler(ClientHandler handler);

    void sendMsg(String message);
}
