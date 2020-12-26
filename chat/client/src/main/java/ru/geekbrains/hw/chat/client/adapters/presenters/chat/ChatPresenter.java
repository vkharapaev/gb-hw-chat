package ru.geekbrains.hw.chat.client.adapters.presenters.chat;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Platform;
import ru.geekbrains.hw.chat.client.ClientApp;
import ru.geekbrains.hw.chat.client.usecases.interactors.ClientInteractor;
import ru.geekbrains.hw.chat.client.usecases.interactors.ClientInteractorImpl;
import ru.geekbrains.hw.chat.client.usecases.interactors.ClientListInteractor;
import ru.geekbrains.hw.chat.client.utils.MessageQueue;

public class ChatPresenter implements ChatContract.Presenter {

    private ChatContract.View view;
    private final ClientInteractor clientInteractor;
    private MessageQueue messageQueue;
    private MessageQueue clientsMessageQueue;
    private final ClientListInteractor clientListInteractor;
    private final CompositeDisposable disposables;

    public ChatPresenter() {
        this.clientInteractor = ClientApp.getInstance().getClient();
        this.clientListInteractor = new ClientListInteractor();
        this.disposables = new CompositeDisposable();
    }

    @Override
    public void takeView(ChatContract.View view) {
        this.view = view;
        readMessages();
        readClients();
    }

    private void readClients() {
        clientsMessageQueue = new MessageQueue(clientInteractor.getClientsMessageQueue());
        clientsMessageQueue.start(clientMessage -> Platform.runLater(() -> {
            clientListInteractor.fillList(clientMessage);
            view.showClients(clientListInteractor.getNickList());
        }));
    }

    private void readMessages() {
        messageQueue = new MessageQueue(clientInteractor.getMessageQueue());
        messageQueue.start(message -> {
            if (message.startsWith(ClientInteractorImpl.MSG_END_CHAT)) {
                onClose();
            } else {
                Platform.runLater(() -> view.appendToChat(message + "\n"));
            }
        });
    }

    private void onClose() {
        disposables.clear();
        messageQueue.stop();
        clientsMessageQueue.stop();
        Platform.runLater(() -> view.goToLoginWindow());
    }

    @Override
    public void sendMessage(String message) {
        if (message != null && !message.isEmpty()) {
            Disposable disposable = clientInteractor.sendMessage(message)
                    .subscribeOn(Schedulers.io())
                    .observeOn(JavaFxScheduler.platform())
                    .subscribe(() -> view.clearMessageField());
            disposables.add(disposable);
        }
    }

}
