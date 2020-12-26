package ru.geekbrains.hw.chat.client.adapters.presenters.login;

import ru.geekbrains.hw.chat.client.adapters.presenters.BasePresenter;
import ru.geekbrains.hw.chat.client.adapters.presenters.BaseView;

public class LoginContract {
    public interface View extends BaseView<Presenter> {
        void showError(String message);

        void goToAuthWindow();

        void goToRegWindow();
    }

    public interface Presenter extends BasePresenter<View> {
        void logIn(String login, String pass);

        void signUp();
    }
}
