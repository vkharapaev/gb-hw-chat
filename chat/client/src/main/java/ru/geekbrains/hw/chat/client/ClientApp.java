package ru.geekbrains.hw.chat.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * VM OPTIONS
 * --module-path ${PATH_TO_FX} --add-modules javafx.controls,javafx.fxml
 */
public class ClientApp extends Application {

    public static final String LAYOUT_CHAT = "ui/chat/chat_layout.fxml";
    public static final String LAYOUT_LOGIN = "ui/login/aut_layout.fxml";

    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 600;

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8189;

    private static ClientApp instance;

    private Client client;
    private Scene scene;

    public static void main(String[] args) {
        launch(args);
    }

    public static ClientApp getInstance() {
        return instance;
    }

    public ClientApp() {
        instance = this;
    }

    @Override
    public void init() {
        List<String> args = getParameters().getUnnamed();
        client = args.size() != 2 ?
                new Client(DEFAULT_HOST, DEFAULT_PORT) :
                new Client(args.get(0), Integer.parseInt(args.get(1)));
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setMinWidth(WINDOW_WIDTH);
        primaryStage.setMinHeight(WINDOW_HEIGHT);
        URL resource = getClass().getResource(LAYOUT_LOGIN);
        Parent root = FXMLLoader.load(resource);
        primaryStage.setTitle(String.format("The Chat %s:%d", client.getHost(), client.getPort()));
        scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void switchToWindow(String layout) {
        try {
            scene.setRoot(FXMLLoader.load(getClass().getResource(layout)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Client getClient() {
        return client;
    }
}
