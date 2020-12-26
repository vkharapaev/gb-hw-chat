package ru.geekbrains.hw.chat.client.adapters.presenters.chat;

import ru.geekbrains.hw.chat.client.adapters.presenters.BasePresenter;
import ru.geekbrains.hw.chat.client.adapters.presenters.BaseView;

import java.util.List;

public class ChatContract {
    public interface View extends BaseView<Presenter> {

        /**
         * Get a message from the message text field.
         *
         * @return Message
         */
        String getMessage();

        /**
         * Clear a message in the message text field
         */
        void clearMessageField();

        /**
         * Append a message to the chat
         *
         * @param message Message, that should be appended
         */
        void appendToChat(String message);

        /**
         * Open the login window
         */
        void goToLoginWindow();

        /**
         * Show clients
         * @param clients List of clients
         */
        void showClients(List<String> clients);
    }

    public interface Presenter extends BasePresenter<View> {

        /**
         * Send a message to the chat
         */
        void sendMessage();
    }
}
