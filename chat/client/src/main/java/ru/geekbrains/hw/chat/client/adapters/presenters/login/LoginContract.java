package ru.geekbrains.hw.chat.client.adapters.presenters.login;

import ru.geekbrains.hw.chat.client.adapters.presenters.BasePresenter;
import ru.geekbrains.hw.chat.client.adapters.presenters.BaseView;

public class LoginContract {
    public interface View extends BaseView<Presenter> {
        String getLogin();

        String getPass();

        void showError(String message);

        void goToChatWindow();
    }

    public interface Presenter extends BasePresenter<View> {
        void signIn();
    }
}
