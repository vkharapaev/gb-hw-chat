package ru.geekbrains.hw.chat.client.ui.chat;

import javafx.application.Platform;
import ru.geekbrains.hw.chat.client.Client;
import ru.geekbrains.hw.chat.client.ClientApp;

public class ChatPresenter implements ChatContract.Presenter {

    private ChatContract.View view;
    private final Client client;

    public ChatPresenter() {
        this.client = ClientApp.getInstance().getClient();
    }

    @Override
    public void takeView(ChatContract.View view) {
        this.view = view;
        client.setMessageListener(msg -> Platform.runLater(() -> view.appendToChat(msg + "\n")));
        client.setAuthorizationListener(isAuthorized -> Platform.runLater(() -> processAuthorizationChange(isAuthorized)));
    }

    @Override
    public void sendMessage() {
        String message = view.getMessage();
        if (message != null && !message.isEmpty()) {
            view.clearMessageField();
            client.sendMsg(message);
        }
    }

    public void processAuthorizationChange(boolean isAuthorized) {
        if (!isAuthorized) {
            view.goToLoginWindow();
        }
    }
}
