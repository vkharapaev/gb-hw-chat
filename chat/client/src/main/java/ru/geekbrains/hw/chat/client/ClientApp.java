package ru.geekbrains.hw.chat.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.geekbrains.hw.chat.client.adapters.data.HistoryRepositoryImpl;
import ru.geekbrains.hw.chat.client.frameworks.ClientImpl;
import ru.geekbrains.hw.chat.client.frameworks.HistoryDataSourceImpl;
import ru.geekbrains.hw.chat.client.usecases.Client;
import ru.geekbrains.hw.chat.client.usecases.interactors.HistoryInteractorImpl;
import ru.geekbrains.hw.chat.client.usecases.interactors.HistoryInteractor;
import ru.geekbrains.hw.chat.client.usecases.interactors.ClientInteractor;
import ru.geekbrains.hw.chat.client.usecases.interactors.ClientInteractorImpl;

import java.io.IOException;
import java.util.List;

/**
 * VM OPTIONS
 * --module-path ${PATH_TO_FX} --add-modules javafx.controls,javafx.fxml
 */
public class ClientApp extends Application {

    public static final String LAYOUT_CHAT = "layouts/chat/chat_layout.fxml";
    public static final String LAYOUT_LOGIN = "layouts/login/aut_layout.fxml";

    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 600;

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8189;

    private static ClientApp instance;

    private Client client;
    private Scene scene;
    private ClientInteractor clientInteractor;

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
        HistoryInteractor historyInteractor =
                new HistoryInteractorImpl(new HistoryRepositoryImpl(new HistoryDataSourceImpl()));
        clientInteractor = new ClientInteractorImpl(historyInteractor);
        client = args.size() != 2 ?
                new ClientImpl(clientInteractor, DEFAULT_HOST, DEFAULT_PORT) :
                new ClientImpl(clientInteractor, args.get(0), Integer.parseInt(args.get(1)));
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setMinWidth(WINDOW_WIDTH);
        primaryStage.setMinHeight(WINDOW_HEIGHT);
        Parent root = FXMLLoader.load(getClass().getResource(LAYOUT_LOGIN));
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

    public ClientInteractor getClient() {
        return clientInteractor;
    }
}
