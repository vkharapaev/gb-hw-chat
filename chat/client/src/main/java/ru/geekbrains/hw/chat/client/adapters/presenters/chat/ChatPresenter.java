package ru.geekbrains.hw.chat.client.adapters.presenters.chat;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ru.geekbrains.hw.chat.client.usecases.interactors.ClientInteractor;
import ru.geekbrains.hw.chat.client.usecases.interactors.ClientInteractorImpl;
import ru.geekbrains.hw.chat.client.usecases.interactors.ClientListInteractor;
import ru.geekbrains.hw.chat.client.utils.JavaFx;
import ru.geekbrains.hw.chat.client.utils.MessageQueue;
import ru.geekbrains.hw.chat.client.utils.Schedulers;

public class ChatPresenter implements ChatContract.Presenter {

    private final ClientInteractor clientInteractor;
    private final JavaFx javaFx;
    private final Schedulers schedulers;
    private final ClientListInteractor clientListInteractor;
    private final CompositeDisposable disposables;
    private ChatContract.View view;
    private MessageQueue messageQueue;
    private MessageQueue clientsMessageQueue;

    public ChatPresenter(ClientInteractor clientInteractor, JavaFx javaFx, Schedulers schedulers) {
        this.clientInteractor = clientInteractor;
        this.javaFx = javaFx;
        this.schedulers = schedulers;
        this.clientListInteractor = new ClientListInteractor();
        this.disposables = new CompositeDisposable();
    }

    @Override
    public void takeView(ChatContract.View view) {
        this.view = view;
        readMessages();
        readClients();
    }

    private void readMessages() {
        messageQueue = clientInteractor.getMessageQueue();
        messageQueue.start(message -> {
            if (message.startsWith(ClientInteractorImpl.MSG_END_CONNECTION)) {
                close();
            } else {
                javaFx.runLater(() -> view.appendToChat(message + "\n"));
            }
        });
    }

    private void readClients() {
        clientsMessageQueue = clientInteractor.getClientsMessageQueue();
        clientsMessageQueue.start(clientMessage -> javaFx.runLater(() -> {
            clientListInteractor.fillList(clientMessage);
            view.showClients(clientListInteractor.getNickList());
        }));
    }

    @Override
    public void sendMessage(String message) {
        if (message != null && !message.isEmpty()) {
            Disposable disposable = schedulers.subscribeOnIoObserveOnJavaFx(clientInteractor.sendMessage(message))
                    .subscribe(() -> view.clearMessageField());
            disposables.add(disposable);
        }
    }

    private void close() {
        disposables.clear();
        messageQueue.stop();
        clientsMessageQueue.stop();
        javaFx.runLater(() -> view.goToLoginWindow());
    }

}
