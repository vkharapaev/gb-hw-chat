package ru.geekbrains.hw.chat.server.usecases.interactors;

import ru.geekbrains.hw.chat.server.entities.User;
import ru.geekbrains.hw.chat.server.usecases.ClientHandler;

import java.io.IOException;

public class ClientHandlerInteractorImpl implements ClientHandlerInteractor {

    private final ServerInteractor serverInteractor;
    private ClientHandler clientHandler;
    private User user;

    public ClientHandlerInteractorImpl(ServerInteractor serverInteractor) {
        this.serverInteractor = serverInteractor;
    }

    @Override
    public String getName() {
        return user.getNick();
    }

    @Override
    public void onStart() {
        try {
            authenticate();
            readMessages();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private boolean isAuthorized() {
        return user != null;
    }

    private void closeConnection() {
        if (isAuthorized()) {
            serverInteractor.unsubscribe(this);
            serverInteractor.broadcast(user.getNick() + " has left the chat.");
            serverInteractor.broadcastClientsList();
        }
        clientHandler.closeConnection();
    }

    @Override
    public void setClientHandler(ClientHandler handler) {
        clientHandler = handler;
    }

    @Override
    public void sendMsg(String message) {
        clientHandler.sendMsg(message);
    }

    private void authenticate() throws IOException {
        clientHandler.setTimer();
        while (true) {
            String message = clientHandler.readMessage();
            if (message.startsWith("/auth")) {
                String[] parts = message.split("\\s", 3);
                if (parts.length == 3) {
                    user = serverInteractor.getUserRepository().getUser(parts[1], parts[2]);
                }
                if (checkClient()) {
                    return;
                }
            } else if (message.startsWith("/reg")) {
                String[] parts = message.split("\\s", 4);
                if (parts.length == 4) {
                    user = serverInteractor.getUserRepository().createUser(parts[1], parts[2], parts[3]);
                }
                if (checkClient()) {
                    return;
                }
            }
        }
    }

    private boolean checkClient() {
        if (user != null) {
            if (serverInteractor.subscribe(this, user.getNick())) {
                clientHandler.sendMsg("/authok " + user.getNick());
                serverInteractor.broadcastClientsList();
                serverInteractor.broadcast(user.getNick() + " entered the chat");
                clientHandler.cancelTimer();
                return true;
            } else {
                clientHandler.sendMsg("The account is already in use.");
                user = null;
            }
        } else {
            clientHandler.sendMsg("The login/pass is not correct.");
        }
        return false;
    }

    private void readMessages() throws IOException {
        while (true) {
            String message = clientHandler.readMessage();
            System.out.printf("from %s: %s\n", user.getNick(), message);
            if (message.startsWith("/")) {
                if (message.equals("/end")) {
                    return;
                }
                if (message.startsWith("/w ")) {
                    String[] parts = message.split("\\s", 3);
                    if (parts.length == 3) {
                        serverInteractor.sendMsgToClient(this, parts[1], parts[2]);
                    }
                }
                if (message.startsWith("/cn ")) {
                    String[] parts = message.split("\\s", 2);
                    if (parts.length == 2 && !parts[1].isEmpty()) {
                        changeNick(parts[1]);
                    }
                }
                continue;
            }
            serverInteractor.broadcast(String.format("%s: %s", user.getNick(), message));
        }
    }

    private void changeNick(String newNick) {
        if (!user.getNick().equals(newNick) &&
                serverInteractor.getUserRepository().changeNick(user.getId(), newNick)) {
            serverInteractor.broadcast(user.getNick() + " changed nick to " + newNick);
            user.setNick(newNick);
        }
    }
}
