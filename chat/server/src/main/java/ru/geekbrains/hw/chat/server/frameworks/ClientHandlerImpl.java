package ru.geekbrains.hw.chat.server.frameworks;

import ru.geekbrains.hw.chat.server.usecases.ClientHandler;
import ru.geekbrains.hw.chat.server.usecases.interactors.ClientHandlerInteractor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandlerImpl implements ClientHandler {

    private static final int TIME_OUT = 120000;

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    public ClientHandlerImpl(ServerImpl server, Socket socket, ClientHandlerInteractor interactor) {
        System.out.printf("The client %s:%d connected.\n", socket.getInetAddress().getHostAddress(), socket.getPort());

        this.socket = socket;
        interactor.setClientHandler(this);

        try {
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        server.getExecutorService().execute(interactor::onStart);
    }

    @Override
    public void setTimer() {
        try {
            socket.setSoTimeout(TIME_OUT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancelTimer() {
        try {
            socket.setSoTimeout(0);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMsg(String msg){
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String readMessage() throws IOException {
        return in.readUTF();
    }

    @Override
    public void closeConnection() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
