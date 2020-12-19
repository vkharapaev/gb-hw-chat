package ru.geekbrains.hw.chat.client.ui.login;

import ru.geekbrains.hw.chat.client.ui.BasePresenter;
import ru.geekbrains.hw.chat.client.ui.BaseView;

public class LoginContract {
    interface View extends BaseView<Presenter> {
        String getLogin();

        String getPass();

        void showError(String message);

        void goToChatWindow();
    }

    interface Presenter extends BasePresenter<View> {
        void signIn();
    }
}
