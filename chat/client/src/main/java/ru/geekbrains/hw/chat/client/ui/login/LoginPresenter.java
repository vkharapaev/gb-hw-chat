package ru.geekbrains.hw.chat.client.ui.login;

import javafx.application.Platform;
import ru.geekbrains.hw.chat.client.Client;
import ru.geekbrains.hw.chat.client.ClientApp;
import ru.geekbrains.hw.chat.client.MessageQueue;
import ru.geekbrains.hw.chat.utils.Util;

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View view;
    private final Client client;
    private MessageQueue messageQueue;

    public LoginPresenter() {
        client = ClientApp.getInstance().getClient();
        client.setAuthorizationListener(isAuthorized -> Platform.runLater(() -> processAuthorizationChange(isAuthorized)));
    }

    @Override
    public void takeView(LoginContract.View view) {
        this.view = view;
        readMessages();
    }

    private void readMessages() {
        messageQueue = new MessageQueue(client.getMessageQueue());
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
        client.signIn(login, pass);
    }

    public void processAuthorizationChange(boolean isAuthorized) {
        if (isAuthorized) {
            messageQueue.stop();
            view.goToChatWindow();
        }
    }
}
