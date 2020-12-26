package ru.geekbrains.hw.chat.client.adapters.presenters.login;

import javafx.event.ActionEvent;
import ru.geekbrains.hw.chat.client.adapters.presenters.BasePresenter;
import ru.geekbrains.hw.chat.client.adapters.presenters.BaseView;

public class LoginContract {
    public interface View extends BaseView<Presenter> {
        void signIn();

        String getLogin();

        String getPass();

        void showError(String message);

        void goToAuthWindow();

        void goToRegWindow();

        void signJoin(ActionEvent actionEvent);
    }

    public interface Presenter extends BasePresenter<View> {
        void signIn();

        void signUp();
    }
}
