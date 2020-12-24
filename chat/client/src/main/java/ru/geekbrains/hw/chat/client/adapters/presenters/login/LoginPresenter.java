package ru.geekbrains.hw.chat.client.adapters.presenters.login;

import javafx.application.Platform;
import ru.geekbrains.hw.chat.client.ClientApp;
import ru.geekbrains.hw.chat.client.utils.MessageQueue;
import ru.geekbrains.hw.chat.client.usecases.interactors.ClientInteractor;
import ru.geekbrains.hw.chat.utils.Util;

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View view;
    private final ClientInteractor clientInteractor;
    private MessageQueue messageQueue;

    public LoginPresenter() {
        clientInteractor = ClientApp.getInstance().getClient();
        clientInteractor.setAuthorizationListener(isAuthorized ->
                Platform.runLater(() -> processAuthorizationChange(isAuthorized)));
    }

    @Override
    public void takeView(LoginContract.View view) {
        this.view = view;
        readMessages();
    }

    private void readMessages() {
        messageQueue = new MessageQueue(clientInteractor.getMessageQueue());
        messageQueue.start(message -> Platform.runLater(() -> view.showError(message)));
    }

    @Override
    public void signIn() {
        String login = Util.nvl(view.getLogin(), "").trim();
        String pass = Util.nvl(view.getPass(), "").trim();

        if (login.isEmpty() || pass.isEmpty()) {
            view.showError("Please, enter a login and password");
            return;
        }

        view.showError("Connecting...");
        clientInteractor.signIn(login, pass);
    }

    public void processAuthorizationChange(boolean isAuthorized) {
        if (isAuthorized) {
            messageQueue.stop();
            view.goToChatWindow();
        }
    }
}