package ru.geekbrains.hw.chat.client.usecases.interactors;

import ru.geekbrains.hw.chat.client.usecases.Client;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientInteractorImpl implements ClientInteractor {
    private static final int HISTORY_LINES_COUNT = 100;
    private final BlockingQueue<String> messageQueue;
    private final BlockingQueue<String> clientsMessageQueue;
    private final HistoryInteractor historyInteractor;
    private final Companion companion;
    private Client client;

    public ClientInteractorImpl(HistoryInteractor historyInteractor) {
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
    public BlockingQueue<String> getMessageQueue() {
        return messageQueue;
    }

    @Override
    public BlockingQueue<String> getClientsMessageQueue() {
        return clientsMessageQueue;
    }

    @Override
    public void sendMsg(String message) {
        client.sendMsg(message);
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
                    messageQueue.put(MSG_END_CHAT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void authenticate() throws IOException, InterruptedException {
        while (true) {
            String strFromServer = client.readMessage();
            if (strFromServer.startsWith("/authok")) {
                messageQueue.put(MSG_END_AUTH);
                break;
            }
            System.out.printf("from server: %s\n", strFromServer);
            messageQueue.put(strFromServer);
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
            if (message.startsWith("/clients")) {
                clientsMessageQueue.put(message);
                continue;
            }
            messageQueue.put(message);
            historyInteractor.storeMessage(message);
        }
    }

    @Override
    public void signIn(String login, String pass) {
        synchronized (companion) {
            if (client.isAuthInProgress()) {
                return;
            }
            client.startSignInTask(() -> {
                if (client.isConnectionClosed()) {
                    start();
                }
                try {
                    if (!client.isConnectionClosed()) {
                        client.sendMsg(String.format("/auth %s %s", login, pass));
                        companion.setLogin(login);
                    } else {
                        messageQueue.put("The server is not responding.");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
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
