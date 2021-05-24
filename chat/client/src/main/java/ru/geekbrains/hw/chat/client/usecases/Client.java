package ru.geekbrains.hw.chat.client.usecases;

import java.io.IOException;

public interface Client {

    String readMessage() throws IOException;

    void sendMsg(String message);

    void start(Runnable startTask);

    void closeConnection();

    boolean isConnectionClosed();

    String getHost();

    int getPort();
}
