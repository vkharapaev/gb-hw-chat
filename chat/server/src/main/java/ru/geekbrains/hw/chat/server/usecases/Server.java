package ru.geekbrains.hw.chat.server.usecases;

import java.io.IOException;

public interface Server {
    void waitForClient();

    int getLocalPort();

    void start() throws IOException;
}
