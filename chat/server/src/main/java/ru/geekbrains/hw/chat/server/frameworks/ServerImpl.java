package ru.geekbrains.hw.chat.server.frameworks;

import ru.geekbrains.hw.chat.server.usecases.Server;
import ru.geekbrains.hw.chat.server.usecases.interactors.ClientHandlerInteractorImpl;
import ru.geekbrains.hw.chat.server.usecases.interactors.ServerInteractor;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ServerImpl implements Server {

    private static final int MAX_SIMULTANEOUS_USER_COUNT = 1000;
    private final ServerSocket serverSocket;
    private final ServerInteractor interactor;
    private final ExecutorService exec;

    public ServerImpl(int port, ServerInteractor interactor) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.interactor = interactor;
        this.interactor.setServer(this);
        this.exec = Executors.newFixedThreadPool(MAX_SIMULTANEOUS_USER_COUNT, new MyThreadFactory());
    }

    @Override
    public void start() throws IOException {
        try {
            interactor.start();
        } finally {
            serverSocket.close();
            exec.shutdown();
        }
    }

    @Override
    public void waitForClient() {
        try {
            new ClientHandlerImpl(this, serverSocket.accept(), new ClientHandlerInteractorImpl(interactor));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getLocalPort() {
        return serverSocket.getLocalPort();
    }

    public ExecutorService getExecutorService() {
        return exec;
    }

    private static final class MyThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable task) {
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            return thread;
        }
    }
}
