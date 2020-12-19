package ru.geekbrains.hw.chat.client.ui.login;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import ru.geekbrains.hw.chat.client.ClientApp;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginView implements Initializable, LoginContract.View {

    @FXML
    private Text error;

    @FXML
    private TextField login;

    @FXML
    private PasswordField pass;

    private final LoginPresenter presenter;

    public LoginView() {
        presenter = new LoginPresenter();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        presenter.takeView(this);
    }

    public void signIn() {
        presenter.signIn();
    }

    @Override
    public String getLogin() {
        return login.getText();
    }

    @Override
    public String getPass() {
        return pass.getText();
    }

    @Override
    public void showError(String message) {
        error.setText(message);
    }

    @Override
    public void goToChatWindow() {
        ClientApp.getInstance().switchToWindow(ClientApp.LAYOUT_CHAT);
    }
}
