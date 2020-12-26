package ru.geekbrains.hw.chat.client.frameworks.views;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import ru.geekbrains.hw.chat.client.ClientApp;
import ru.geekbrains.hw.chat.client.adapters.presenters.reg.RegContract;
import ru.geekbrains.hw.chat.client.adapters.presenters.reg.RegPresenter;

import java.net.URL;
import java.util.ResourceBundle;

public class RegView implements Initializable, RegContract.View {
    @FXML
    public TextField nick;

    @FXML
    private Text error;

    @FXML
    private TextField login;

    @FXML
    private PasswordField pass;

    private final RegPresenter presenter;

    public RegView() {
        presenter = new RegPresenter();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        presenter.takeView(this);
    }

    @Override
    public void showError(String message) {
        error.setText(message);
    }

    @Override
    public void goToChatWindow() {
        ClientApp.getInstance().switchToWindow(ClientApp.LAYOUT_CHAT);
    }

    @Override
    public void goToLoginWindow() {
        ClientApp.getInstance().switchToWindow(ClientApp.LAYOUT_LOGIN);
    }

    public void goBack() {
        presenter.goBack();
    }

    public void join() {
        presenter.join(login.getText(), pass.getText(), nick.getText());
    }
}
