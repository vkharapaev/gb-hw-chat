package ru.geekbrains.hw.chat.client.usecases.interactors;

import ru.geekbrains.hw.chat.client.usecases.Client;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientInteractorImpl implements ClientInteractor {
    private static final int HISTORY_LINES_COUNT = 100;
    private final BlockingQueue<String> messageQueue;
    private final HistoryInteractor historyInteractor;
    private Client client;
    private final Companion companion;

    public ClientInteractorImpl(HistoryInteractor historyInteractor) {
        this.historyInteractor = historyInteractor;
        this.messageQueue = new LinkedBlockingQueue<>();
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
    public void sendMsg(String message) {
        client.sendMsg(message);
    }

    @Override
    public void start() {
        setAuthorized(false);
        client.start(() -> {
            try {
                authenticate();
                showHistory();
                readMessages();
            } catch (IOException | InterruptedException e) {
                setAuthorized(false);
                e.printStackTrace();
                client.closeConnection();
            }
        });
    }

    private void authenticate() throws IOException, InterruptedException {
        while (true) {
            String strFromServer = client.readMessage();
            if (strFromServer.startsWith("/authok")) {
                setAuthorized(true);
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
            String strFromServer = client.readMessage();
            if (strFromServer.equalsIgnoreCase("/end")) {
                break;
            }
            System.out.printf("from server2: %s\n", strFromServer);
            messageQueue.put(strFromServer);
            historyInteractor.storeMessage(strFromServer);
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

    public void setAuthorized(boolean authorized) {
        synchronized (companion) {
            boolean tmpAuthorized = companion.isAuthorized();
            companion.setAuthorized(authorized);
            if (tmpAuthorized != authorized && companion.getAuthorizationListener() != null) {
                companion.getAuthorizationListener().onChange(authorized);
            }
        }
    }

    public void setAuthorizationListener(OnAuthorizationChanged authorizationListener) {
        companion.setAuthorizationListener(authorizationListener);
    }

    private static class Companion {
        private boolean authorized;
        private String login;
        private OnAuthorizationChanged authorizationListener;

        synchronized boolean isAuthorized() {
            return authorized;
        }

        synchronized void setAuthorized(boolean authorized) {
            this.authorized = authorized;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public OnAuthorizationChanged getAuthorizationListener() {
            return authorizationListener;
        }

        public void setAuthorizationListener(OnAuthorizationChanged authorizationListener) {
            this.authorizationListener = authorizationListener;
        }
    }

}
