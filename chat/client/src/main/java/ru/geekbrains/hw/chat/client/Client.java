package ru.geekbrains.hw.chat.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class Client {

    private volatile boolean authorized;
    private volatile Socket socket;
    private volatile DataInputStream in;
    private volatile DataOutputStream out;

    private volatile OnAuthorizationChanged authorizationListener;
    private volatile OnMessageReceived messageListener;
    private final String host;
    private final int port;
    private Thread signInThread;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        try {
            socket = new Socket(host, port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            setAuthorized(false);
            Thread t = new Thread(() -> {
                try {
                    while (true) {
                        String strFromServer = in.readUTF();
                        if (strFromServer.startsWith("/authok")) {
                            setAuthorized(true);
                            TimeUnit.SECONDS.sleep(1);
                            break;
                        }
                        System.out.printf("from server: %s\n", strFromServer);
                        notifyAboutMessage(strFromServer);
                    }
                    while (true) {
                        String strFromServer = in.readUTF();
                        if (strFromServer.equalsIgnoreCase("/end")) {
                            break;
                        }
                        System.out.printf("from server: %s\n", strFromServer);
                        notifyAboutMessage(strFromServer);
                    }
                } catch (IOException | InterruptedException e) {
                    setAuthorized(false);
                    e.printStackTrace();
                    closeConnection();
                }
            });
            t.setDaemon(true);
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void signIn(String login, String pass) {
        if (signInThread != null && signInThread.isAlive()) {
            return;
        }
        signInThread = new Thread(() -> {
            if (socket == null || socket.isClosed()) {
                start();
            }
            try {
                if (socket != null && !socket.isClosed()) {
                    out.writeUTF(String.format("/auth %s %s", login, pass));
                } else {
                    notifyAboutMessage("The server is not responding.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        signInThread.start();
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAuthorized(boolean authorized) {
        boolean tmpAuthorized = this.authorized;
        this.authorized = authorized;
        if (tmpAuthorized != authorized && authorizationListener != null) {
            authorizationListener.onChange(authorized);
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void setAuthorizationListener(OnAuthorizationChanged authorizationListener) {
        this.authorizationListener = authorizationListener;
    }

    public void setMessageListener(OnMessageReceived messageListener) {
        this.messageListener = messageListener;
    }

    private void notifyAboutMessage(String msg) {
        if (messageListener != null) {
            messageListener.onReceived(msg);
        }
    }

    public interface OnAuthorizationChanged {
        void onChange(boolean isAuthorized);
    }

    public interface OnMessageReceived {
        void onReceived(String msg);
    }

}
