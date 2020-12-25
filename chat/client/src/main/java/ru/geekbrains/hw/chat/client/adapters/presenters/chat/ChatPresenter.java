package ru.geekbrains.hw.chat.client.adapters.presenters.chat;

import javafx.application.Platform;
import ru.geekbrains.hw.chat.client.ClientApp;
import ru.geekbrains.hw.chat.client.usecases.interactors.ClientInteractorImpl;
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
        readMessages();
    }

    private void readMessages() {
        messageQueue = new MessageQueue(clientInteractor.getMessageQueue());
        messageQueue.start(message -> {
            if (message.startsWith(ClientInteractorImpl.MSG_END_CHAT)) {
                messageQueue.stop();
                Platform.runLater(() -> view.goToLoginWindow());
            } else {
                Platform.runLater(() -> view.appendToChat(message + "\n"));
            }
        });
    }

    @Override
    public void sendMessage() {
        String message = view.getMessage();
        if (message != null && !message.isEmpty()) {
            view.clearMessageField();
            clientInteractor.sendMsg(message);
        }
    }
}
