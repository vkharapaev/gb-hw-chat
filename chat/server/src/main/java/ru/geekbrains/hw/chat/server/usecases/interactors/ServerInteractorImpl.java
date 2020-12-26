package ru.geekbrains.hw.chat.server.usecases.interactors;

import ru.geekbrains.hw.chat.ChatCommands;
import ru.geekbrains.hw.chat.server.usecases.Repository;
import ru.geekbrains.hw.chat.server.usecases.Server;

import java.util.ArrayList;
import java.util.List;

public class ServerInteractorImpl implements ServerInteractor {

    private final List<ClientHandlerInteractor> clients;
    private Server server;
    private final Repository repository;

    public ServerInteractorImpl(Repository repository) {
        this.repository = repository;
        clients = new ArrayList<>();
    }

    @Override
    public void setServer(Server server) {
        this.server = server;
    }

    @Override
    public void start() {
        try {
            repository.start();
            while (true) {
                System.out.printf("The server is waiting for connections on %d port...\n", server.getLocalPort());
                server.waitForClient();
            }
        } finally {
            repository.stop();
        }
    }

    @Override
    public Repository getUserRepository() {
        return repository;
    }

    @Override
    public synchronized boolean isNickBusy(String nick) {
        for (ClientHandlerInteractor handler : clients) {
            if (handler.getName().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized void broadcast(String message) {
        for (ClientHandlerInteractor handler : clients) {
            handler.sendMsg(message);
        }
    }

    @Override
    public synchronized void sendMsgToClient(ClientHandlerInteractor from, String toNick, String message) {
        for (ClientHandlerInteractor handler : clients) {
            if (handler.getName().equals(toNick)) {
                handler.sendMsg("from " + from.getName() + ": " + message);
                from.sendMsg("to " + toNick + ": " + message);
                return;
            }
        }
        from.sendMsg(String.format("%s is not connected.", toNick));
    }

    @Override
    public synchronized void broadcastClientsList() {
        StringBuilder sb = new StringBuilder(ChatCommands.SERVER_CLIENTS);
        for (ClientHandlerInteractor handler : clients) {
            sb.append(" ").append(handler.getName());
        }
        broadcast(sb.toString());
    }

    @Override
    public synchronized void unsubscribe(ClientHandlerInteractor handler) {
        clients.remove(handler);
    }

    @Override
    public synchronized boolean subscribe(ClientHandlerInteractor handler, String nick) {
        if (isNickBusy(nick)) {
            return false;
        }
        clients.add(handler);
        return true;
    }
}
