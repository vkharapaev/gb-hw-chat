package ru.geekbrains.hw.chat.client.adapters.presenters.reg;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Platform;
import ru.geekbrains.hw.chat.client.ClientApp;
import ru.geekbrains.hw.chat.client.usecases.interactors.ClientInteractor;
import ru.geekbrains.hw.chat.client.usecases.interactors.ClientInteractorImpl;
import ru.geekbrains.hw.chat.client.utils.MessageQueue;
import ru.geekbrains.hw.chat.utils.Util;

public class RegPresenter implements RegContract.Presenter {

    private RegContract.View view;
    private final ClientInteractor clientInteractor;
    private MessageQueue messageQueue;
    private final CompositeDisposable disposables;
    private Disposable regDisposable;

    public RegPresenter() {
        this.clientInteractor = ClientApp.getInstance().getClient();
        this.disposables = new CompositeDisposable();
    }

    @Override
    public void takeView(RegContract.View view) {
        this.view = view;
        readMessages();
    }

    private void readMessages() {
        messageQueue = new MessageQueue(clientInteractor.getMessageQueue());
        messageQueue.start(message -> {
            if (message.startsWith(ClientInteractorImpl.MSG_END_AUTH)) {
                close();
                Platform.runLater(() -> view.goToChatWindow());
            } else {
                Platform.runLater(() -> view.showError(message));
            }
        });
    }

    @Override
    public void goBack() {
        close();
        view.goToLoginWindow();
    }

    @Override
    public void join(String login, String pass, String nick) {

        login = Util.nvl(login, "").trim();
        pass = Util.nvl(pass, "").trim();
        nick = Util.nvl(nick, "").trim();

        if (login.isEmpty() || pass.isEmpty() || nick.isEmpty()) {
            view.showError("Please, enter a login, nick and password");
            return;
        }

        view.showError("Connecting...");

        if (regDisposable == null || regDisposable.isDisposed()) {
            regDisposable = clientInteractor.register(login, nick, pass)
                    .subscribeOn(Schedulers.io())
                    .observeOn(JavaFxScheduler.platform())
                    .subscribe();
            disposables.add(regDisposable);
        }
    }

    private void close() {
        disposables.clear();
        messageQueue.stop();
    }
}
