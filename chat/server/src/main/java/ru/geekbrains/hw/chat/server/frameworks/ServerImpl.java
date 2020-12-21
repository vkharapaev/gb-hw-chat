package ru.geekbrains.hw.chat.server.frameworks;

import ru.geekbrains.hw.chat.server.usecases.Server;
import ru.geekbrains.hw.chat.server.usecases.interactors.ClientHandlerInteractorImpl;
import ru.geekbrains.hw.chat.server.usecases.interactors.ServerInteractor;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerImpl implements Server {

    private final ServerSocket serverSocket;
    private final ServerInteractor interactor;

    public ServerImpl(int port, ServerInteractor interactor) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.interactor = interactor;
        this.interactor.setServer(this);
    }

    @Override
    public void start() throws IOException {
        try {
            interactor.start();
        } finally {
            serverSocket.close();
        }
    }

    @Override
    public void waitForClient() {
        try {
            new ClientHandlerImpl(serverSocket.accept(), new ClientHandlerInteractorImpl(interactor));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getLocalPort() {
        return serverSocket.getLocalPort();
    }
}
