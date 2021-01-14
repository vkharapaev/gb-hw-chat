package ru.geekbrains.hw.chat.client.adapters.presenters.login;

import io.reactivex.Completable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ru.geekbrains.hw.chat.client.usecases.interactors.ClientInteractor;
import ru.geekbrains.hw.chat.client.utils.JavaFx;
import ru.geekbrains.hw.chat.client.utils.MessageQueue;
import ru.geekbrains.hw.chat.client.utils.Schedulers;
import ru.geekbrains.hw.chat.utils.Util;

public class LoginPresenter implements LoginContract.Presenter {

    private final JavaFx javaFx;
    private final ClientInteractor clientInteractor;
    private final Schedulers schedulers;
    private final CompositeDisposable disposables;
    private LoginContract.View view;
    private MessageQueue messageQueue;
    private Disposable signInDisposable;

    public LoginPresenter(ClientInteractor clientInteractor, JavaFx javaFx, Schedulers schedulers) {
        this.javaFx = javaFx;
        this.clientInteractor = clientInteractor;
        this.schedulers = schedulers;
        this.disposables = new CompositeDisposable();
    }

    @Override
    public void takeView(LoginContract.View view) {
        this.view = view;
        readMessages();
    }

    private void readMessages() {
        messageQueue = clientInteractor.getMessageQueue();
        messageQueue.start(message -> {
            if (message.startsWith(ClientInteractor.MSG_END_AUTH)) {
                close();
                javaFx.runLater(() -> view.goToAuthWindow());
            } else {
                javaFx.runLater(() -> view.showError(message));
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
            signInDisposable = schedulers.subscribeOnIoObserveOnJavaFx(clientInteractor.signIn(login, pass))
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
