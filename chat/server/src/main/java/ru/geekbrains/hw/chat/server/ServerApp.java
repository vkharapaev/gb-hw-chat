package ru.geekbrains.hw.chat.server;

import ru.geekbrains.hw.chat.server.adapters.data.RepositoryImpl;
import ru.geekbrains.hw.chat.server.frameworks.ServerImpl;
import ru.geekbrains.hw.chat.server.frameworks.local.LocalDataSource;
import ru.geekbrains.hw.chat.server.usecases.interactors.ServerInteractor;
import ru.geekbrains.hw.chat.server.usecases.interactors.ServerInteractorImpl;

import java.io.IOException;

public class ServerApp {

    private static final int DEFAULT_PORT = 8189;

    public static void main(String[] args) throws IOException {
        int port = args.length != 1 ? DEFAULT_PORT : Integer.parseInt(args[0]);
        ServerInteractor interactor = new ServerInteractorImpl(new RepositoryImpl(new LocalDataSource()));
        new ServerImpl(port, interactor).start();
    }
}
