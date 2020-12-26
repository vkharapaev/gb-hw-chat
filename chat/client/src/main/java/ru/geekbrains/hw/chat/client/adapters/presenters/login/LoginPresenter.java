package ru.geekbrains.hw.chat.client.adapters.presenters.login;

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

public class LoginPresenter implements LoginContract.Presenter {

    private final ClientInteractor clientInteractor;
    private final CompositeDisposable disposables;
    private LoginContract.View view;
    private MessageQueue messageQueue;
    private Disposable signInDisposable;

    public LoginPresenter() {
        this.clientInteractor = ClientApp.getInstance().getClient();
        this.disposables = new CompositeDisposable();
    }

    @Override
    public void takeView(LoginContract.View view) {
        this.view = view;
        readMessages();
    }

    private void readMessages() {
        messageQueue = new MessageQueue(clientInteractor.getMessageQueue());
        messageQueue.start(message -> {
            if (message.startsWith(ClientInteractorImpl.MSG_END_AUTH)) {
                close();
                Platform.runLater(() -> view.goToAuthWindow());
            } else {
                Platform.runLater(() -> view.showError(message));
            }
        });
    }

    @Override
    public void logIn(String login, String pass) {
        login = Util.nvl(login, "").trim();
        pass = Util.nvl(pass, "").trim();

        if (login.isEmpty() || pass.isEmpty()) {
            view.showError("Please, enter a login and password");
            return;
        }

        view.showError("Connecting...");

        if (signInDisposable == null || signInDisposable.isDisposed()) {
            signInDisposable = clientInteractor.signIn(login, pass)
                    .subscribeOn(Schedulers.io())
                    .observeOn(JavaFxScheduler.platform())
                    .subscribe();
            disposables.add(signInDisposable);
        }
    }

    @Override
    public void signUp() {
        close();
        view.goToRegWindow();
    }

    private void close() {
        disposables.clear();
        messageQueue.stop();
    }

}
