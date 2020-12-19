package ru.geekbrains.hw.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler {

    private static final int TIME_OUT = 120000;

    private final MyServer myServer;
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private String name;

    public ClientHandler(MyServer server, Socket socket) {
        try {
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("There is a problem during a client handler creation.");
        }

        this.myServer = server;

        Thread thread = new Thread(() -> {
            try {
                authenticate();
                readMessages();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public String getName() {
        return name;
    }

    private void setTimer() throws SocketException {
        socket.setSoTimeout(TIME_OUT);
    }

    private void cancelTimer() throws SocketException {
        socket.setSoTimeout(0);
    }

    private void authenticate() throws IOException {
        setTimer();
        while (true) {
            String str = in.readUTF();
            if (str.startsWith("/auth")) {
                String[] parts = str.split("\\s", 3);
                String nick = null;
                if (parts.length == 3) {
                    nick = myServer.getAuthService().getNickByLoginPass(parts[1], parts[2]);
                }
                if (nick != null) {
                    if (!myServer.isNickBusy(nick)) {
                        sendMsg("/authok " + nick);
                        name = nick;
                        myServer.broadcastMsg(name + " entered the chat");
                        myServer.subscribe(this);
                        cancelTimer();
                        return;
                    } else {
                        sendMsg("The account is already in use.");
                    }
                } else {
                    sendMsg("The login/pass is not correct.");
                }
            }
        }
    }

    private void readMessages() throws IOException {
        while (true) {
            String str = in.readUTF();
            System.out.printf("from %s: %s\n", name, str);
            if (str.startsWith("/")) {
                if (str.equals("/end")) {
                    return;
                }
                if (str.startsWith("/w ")) {
                    String[] parts = str.split("\\s", 3);
                    if (parts.length == 3) {
                        myServer.sendMsgToClient(this, parts[1], parts[2]);
                    }
                }
                continue;
            }
            myServer.broadcastMsg(String.format("%s: %s", name, str));
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isAuthorized() {
        return name != null;
    }

    private void closeConnection() {
        if (isAuthorized()) {
            myServer.unsubscribe(this);
            myServer.broadcastMsg(name + " has left the chat.");
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
