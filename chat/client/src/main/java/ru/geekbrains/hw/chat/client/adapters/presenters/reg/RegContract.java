package ru.geekbrains.hw.chat.client.adapters.presenters.reg;

import ru.geekbrains.hw.chat.client.adapters.presenters.BasePresenter;
import ru.geekbrains.hw.chat.client.adapters.presenters.BaseView;

public class RegContract {
    public interface View extends BaseView<Presenter> {
        void showError(String message);

        void goToChatWindow();

        void goToLoginWindow();
    }

    public interface Presenter extends BasePresenter<View> {
        void goBack();

        void register(String login, String pass, String nick);
    }
}
