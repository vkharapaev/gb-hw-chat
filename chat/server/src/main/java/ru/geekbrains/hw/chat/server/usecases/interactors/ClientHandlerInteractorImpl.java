package ru.geekbrains.hw.chat.server.usecases.interactors;

import org.apache.log4j.Logger;
import ru.geekbrains.hw.chat.ChatCommands;
import ru.geekbrains.hw.chat.server.entities.User;
import ru.geekbrains.hw.chat.server.usecases.ClientHandler;

import java.io.IOException;

public class ClientHandlerInteractorImpl implements ClientHandlerInteractor {

    private static final Logger log = Logger.getLogger(ClientHandlerInteractorImpl.class.getName());
    private final ServerInteractor serverInteractor;
    private ClientHandler clientHandler;
    private User user;

    public ClientHandlerInteractorImpl(ServerInteractor serverInteractor) {
        this.serverInteractor = serverInteractor;
    }

    @Override
    public String getName() {
        return user == null ? null : user.getNick();
    }

    @Override
    public void onStart() {
        try {
            authenticate();
            readMessages();
        } catch (IOException e) {
            log.error("onStart", e);
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
            log.debug(String.format("Authorization message `%s'", message));
            if (message.startsWith(ChatCommands.CLIENT_AUTH)) {
                String[] parts = message.split("\\s", 3);
                if (parts.length == 3) {
                    user = serverInteractor.getUserRepository().getUser(parts[1], parts[2]);
                }
                if (checkClient()) {
                    return;
                }
            } else if (message.startsWith(ChatCommands.CLIENT_REG)) {
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
            if (serverInteractor.subscribe(this)) {
                clientHandler.sendMsg(ChatCommands.SERVER_AUTH_OK + " " + user.getNick());
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
            if (message.startsWith("/")) {
                if (message.equals(ChatCommands.CLIENT_END)) {
                    return;
                } else if (message.startsWith(ChatCommands.CLIENT_PRIVATE_MSG + " ")) {
                    String[] parts = message.split("\\s", 3);
                    if (parts.length == 3) {
                        serverInteractor.sendMsgToClient(this, parts[1], parts[2]);
                    }
                } else if (message.startsWith(ChatCommands.CLIENT_CHANGE_NICK + " ")) {
                    String[] parts = message.split("\\s", 2);
                    if (parts.length == 2 && !parts[1].isEmpty()) {
                        changeNick(parts[1]);
                    }
                }
                continue;
            }
            log.debug(String.format("User `%s' sent message `%s'", user.getNick(), message));
            serverInteractor.broadcast(String.format("%s: %s", user.getNick(), message));
        }
    }

    private void changeNick(String newNick) {
        if (!user.getNick().equals(newNick) &&
                serverInteractor.getUserRepository().changeNick(user.getId(), newNick)) {
            serverInteractor.broadcast(user.getNick() + " changed nick to " + newNick);
            log.debug(String.format("User `%s' changed nick to `%s'", user.getNick(), newNick));
            user.setNick(newNick);
            serverInteractor.broadcastClientsList();
        }
    }
}
