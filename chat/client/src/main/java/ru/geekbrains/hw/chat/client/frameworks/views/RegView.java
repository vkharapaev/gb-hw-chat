package ru.geekbrains.hw.chat.client.frameworks.views;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import ru.geekbrains.hw.chat.client.ClientApp;
import ru.geekbrains.hw.chat.client.adapters.presenters.reg.RegContract;
import ru.geekbrains.hw.chat.client.adapters.presenters.reg.RegPresenter;
import ru.geekbrains.hw.chat.client.utils.JavaFxImpl;
import ru.geekbrains.hw.chat.client.utils.SchedulersImpl;

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
        presenter = new RegPresenter(ClientApp.getInstance().getClient(), new JavaFxImpl(), new SchedulersImpl());
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

    @FXML
    private void goBack() {
        presenter.goBack();
    }

    @FXML
    private void register() {
        presenter.register(login.getText(), pass.getText(), nick.getText());
    }
}
