package ru.geekbrains.hw.chat.client.usecases.interactors;

import io.reactivex.Completable;
import ru.geekbrains.hw.chat.ChatCommands;
import ru.geekbrains.hw.chat.client.usecases.Client;
import ru.geekbrains.hw.chat.client.utils.MessageQueue;
import ru.geekbrains.hw.chat.client.utils.MessageQueueFactory;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientInteractorImpl implements ClientInteractor {

    private static final int HISTORY_LINES_COUNT = 100;
    private final MessageQueueFactory messageQueueFactory;
    private final HistoryInteractor historyInteractor;
    private final BlockingQueue<String> messageQueue;
    private final BlockingQueue<String> clientsMessageQueue;
    private final Companion companion;
    private Client client;

    public ClientInteractorImpl(MessageQueueFactory messageQueueFactory, HistoryInteractor historyInteractor) {
        this.messageQueueFactory = messageQueueFactory;
        this.historyInteractor = historyInteractor;
        this.messageQueue = new LinkedBlockingQueue<>();
        this.clientsMessageQueue = new LinkedBlockingQueue<>();
        this.companion = new Companion();
    }

    @Override
    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public MessageQueue getMessageQueue() {
        return messageQueueFactory.create(messageQueue);
    }

    @Override
    public MessageQueue getClientsMessageQueue() {
        return messageQueueFactory.create(clientsMessageQueue);
    }

    @Override
    public Completable sendMessage(String message) {
        return Completable.fromRunnable(() -> client.sendMsg(message));
    }

    @Override
    public void start() {
        client.start(() -> {
            try {
                authenticate();
                showHistory();
                readMessages();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                client.closeConnection();
            } finally {
                try {
                    messageQueue.put(MSG_END_CONNECTION);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void authenticate() throws IOException, InterruptedException {
        while (true) {
            String message = client.readMessage();
            if (message.startsWith(ChatCommands.SERVER_AUTH_OK)) {
                messageQueue.put(MSG_END_AUTH);
                break;
            }
            System.out.printf("from server: %s\n", message);
            messageQueue.put(message);
        }
    }

    private void showHistory() throws InterruptedException {
        historyInteractor.setSourceName(companion.getLogin());
        for (String message : historyInteractor.getHistory(HISTORY_LINES_COUNT)) {
            messageQueue.put(message);
        }
    }

    private void readMessages() throws IOException, InterruptedException {
        while (true) {
            String message = client.readMessage();
            System.out.printf("from server2: %s\n", message);
            if (message.startsWith(ChatCommands.SERVER_CLIENTS)) {
                clientsMessageQueue.put(message);
                continue;
            }
            messageQueue.put(message);
            historyInteractor.storeMessage(message);
        }
    }

    @Override
    public Completable signIn(String login, String pass) {
        return Completable.fromRunnable(() ->
                sendMessage(login, String.format(ChatCommands.CLIENT_TEMPLATE_AUTH, login, pass)));
    }

    @Override
    public Completable register(String login, String nick, String pass) {
        return Completable.fromRunnable(() ->
                sendMessage(login, String.format(ChatCommands.CLIENT_TEMPLATE_REG, login, nick, pass)));
    }

    private void sendMessage(String login, String message) {
        synchronized (companion) {
            if (client.isConnectionClosed()) {
                start();
            }
            try {
                if (!client.isConnectionClosed()) {
                    client.sendMsg(message);
                    companion.setLogin(login);
                } else {
                    messageQueue.put("The server is not responding.");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Companion {
        private String login;

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }
    }

}
