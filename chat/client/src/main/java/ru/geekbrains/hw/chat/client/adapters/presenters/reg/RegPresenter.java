package ru.geekbrains.hw.chat.client.adapters.presenters.reg;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ru.geekbrains.hw.chat.client.usecases.interactors.ClientInteractor;
import ru.geekbrains.hw.chat.client.usecases.interactors.ClientInteractorImpl;
import ru.geekbrains.hw.chat.client.utils.JavaFx;
import ru.geekbrains.hw.chat.client.utils.MessageQueue;
import ru.geekbrains.hw.chat.client.utils.Schedulers;
import ru.geekbrains.hw.chat.utils.Util;

public class RegPresenter implements RegContract.Presenter {

    private final ClientInteractor clientInteractor;
    private final JavaFx javaFx;
    private final Schedulers schedulers;
    private final CompositeDisposable disposables;
    private RegContract.View view;
    private MessageQueue messageQueue;
    private Disposable regDisposable;

    public RegPresenter(ClientInteractor clientInteractor, JavaFx javaFx, Schedulers schedulers) {
        this.clientInteractor = clientInteractor;
        this.javaFx = javaFx;
        this.schedulers = schedulers;
        this.disposables = new CompositeDisposable();
    }

    @Override
    public void takeView(RegContract.View view) {
        this.view = view;
        readMessages();
    }

    private void readMessages() {
        messageQueue = clientInteractor.getMessageQueue();
        messageQueue.start(message -> {
            if (message.startsWith(ClientInteractorImpl.MSG_END_AUTH)) {
                close();
                javaFx.runLater(() -> view.goToChatWindow());
            } else {
                javaFx.runLater(() -> view.showError(message));
            }
        });
    }

    @Override
    public void goBack() {
        close();
        view.goToLoginWindow();
    }

    @Override
    public void register(String login, String pass, String nick) {

        login = Util.nvl(login, "").trim();
        pass = Util.nvl(pass, "").trim();
        nick = Util.nvl(nick, "").trim();

        if (login.isEmpty() || pass.isEmpty() || nick.isEmpty()) {
            view.showError("Please, fill in all fields");
            return;
        }

        view.showError("Connecting...");

        if (regDisposable == null || regDisposable.isDisposed()) {
            regDisposable = schedulers.subscribeOnIoObserveOnJavaFx(clientInteractor.register(login, nick, pass))
                    .subscribe();
            disposables.add(regDisposable);
        }
    }

    private void close() {
        disposables.clear();
        messageQueue.stop();
    }
}
