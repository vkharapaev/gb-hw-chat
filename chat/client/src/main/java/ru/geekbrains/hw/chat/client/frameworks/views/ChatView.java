package ru.geekbrains.hw.chat.client.frameworks.views;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ru.geekbrains.hw.chat.client.ClientApp;
import ru.geekbrains.hw.chat.client.adapters.presenters.chat.ChatContract;
import ru.geekbrains.hw.chat.client.adapters.presenters.chat.ChatPresenter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ChatView implements Initializable, ChatContract.View {

    @FXML
    private TextArea clientsTextArea;

    @FXML
    private TextArea chatTextArea;

    @FXML
    private TextField messageTextField;

    private ChatContract.Presenter presenter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        presenter = new ChatPresenter();
        presenter.takeView(this);
    }

    @FXML
    private void sendAction() {
        presenter.sendMessage(messageTextField.getText());
    }

    @Override
    public void clearMessageField() {
        messageTextField.clear();
    }

    @Override
    public void appendToChat(String message) {
        chatTextArea.appendText(message);
    }

    @Override
    public void showClients(List<String> clients) {
        StringBuilder sb = new StringBuilder();
        for (String nick : clients) {
            sb.append(nick).append(", ");
        }
        sb.delete(sb.length()-2, sb.length());
        clientsTextArea.setText(sb.toString());
    }

    @Override
    public void goToLoginWindow() {
        ClientApp.getInstance().switchToWindow(ClientApp.LAYOUT_LOGIN);
    }

}
