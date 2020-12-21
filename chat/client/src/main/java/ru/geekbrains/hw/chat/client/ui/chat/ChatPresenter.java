package ru.geekbrains.hw.chat.client.ui.chat;

import javafx.application.Platform;
import ru.geekbrains.hw.chat.client.Client;
import ru.geekbrains.hw.chat.client.ClientApp;
import ru.geekbrains.hw.chat.client.MessageQueue;

public class ChatPresenter implements ChatContract.Presenter {

    private ChatContract.View view;
    private final Client client;
    private MessageQueue messageQueue;

    public ChatPresenter() {
        this.client = ClientApp.getInstance().getClient();
    }

    @Override
    public void takeView(ChatContract.View view) {
        this.view = view;
        client.setAuthorizationListener(isAuthorized -> Platform.runLater(() -> processAuthorizationChange(isAuthorized)));
        readMessages();
    }

    private void readMessages() {
        messageQueue = new MessageQueue(client.getMessageQueue());
        messageQueue.start(message -> Platform.runLater(() -> view.appendToChat(message + "\n")));
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
            messageQueue.stop();
        }
    }
}
