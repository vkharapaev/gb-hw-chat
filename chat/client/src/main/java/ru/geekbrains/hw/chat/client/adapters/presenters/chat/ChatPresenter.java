package ru.geekbrains.hw.chat.client.adapters.presenters.chat;

import javafx.application.Platform;
import ru.geekbrains.hw.chat.client.ClientApp;
import ru.geekbrains.hw.chat.client.utils.MessageQueue;
import ru.geekbrains.hw.chat.client.usecases.interactors.ClientInteractor;

public class ChatPresenter implements ChatContract.Presenter {

    private ChatContract.View view;
    private final ClientInteractor clientInteractor;
    private MessageQueue messageQueue;

    public ChatPresenter() {
        this.clientInteractor = ClientApp.getInstance().getClient();
    }

    @Override
    public void takeView(ChatContract.View view) {
        this.view = view;
        clientInteractor.setAuthorizationListener(isAuthorized -> Platform.runLater(() -> processAuthorizationChange(isAuthorized)));
        readMessages();
    }

    private void readMessages() {
        messageQueue = new MessageQueue(clientInteractor.getMessageQueue());
        messageQueue.start(message -> Platform.runLater(() -> view.appendToChat(message + "\n")));
    }

    @Override
    public void sendMessage() {
        String message = view.getMessage();
        if (message != null && !message.isEmpty()) {
            view.clearMessageField();
            clientInteractor.sendMsg(message);
        }
    }

    public void processAuthorizationChange(boolean isAuthorized) {
        if (!isAuthorized) {
            view.goToLoginWindow();
            messageQueue.stop();
        }
    }
}
