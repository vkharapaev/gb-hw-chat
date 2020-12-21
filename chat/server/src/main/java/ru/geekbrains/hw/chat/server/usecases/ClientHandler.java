package ru.geekbrains.hw.chat.server.usecases;

import java.io.IOException;

public interface ClientHandler {
    void sendMsg(String msg);

    String readMessage() throws IOException;

    void setTimer();

    void cancelTimer();

    void closeConnection();
}
