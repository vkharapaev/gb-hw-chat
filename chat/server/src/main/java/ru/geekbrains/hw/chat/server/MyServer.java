package ru.geekbrains.hw.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {

    private final List<ClientHandler> clients;
    private AuthService authService;

    public MyServer(int port) {
        clients = new ArrayList<>();
        try (ServerSocket server = new ServerSocket(port)) {
            authService = new BaseAuthService();
            authService.start();
            while (true) {
                System.out.printf("The server is waiting for connections on %d port...\n", server.getLocalPort());
                Socket socket = server.accept();
                System.out.printf("The client %s:%d connected.\n",
                        socket.getInetAddress().getHostAddress(), socket.getPort());
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            System.out.println("The server stopped because of an error.");
        } finally {
            if (authService != null) {
                authService.stop();
            }
        }
    }

    public AuthService getAuthService() {
        return authService;
    }

    public synchronized boolean isNickBusy(String nick) {
        for (ClientHandler handler : clients) {
            if (handler.getName().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void broadcastMsg(String msg) {
        for (ClientHandler handler : clients) {
            handler.sendMsg(msg);
        }
    }

    public synchronized void sendMsgToClient(ClientHandler from, String toNick, String msg) {
        for (ClientHandler handler : clients) {
            if (handler.getName().equals(toNick)) {
                handler.sendMsg("from " + from.getName() + ": " + msg);
                from.sendMsg("to " + toNick + ": " + msg);
                return;
            }
        }
        from.sendMsg(String.format("%s is not connected.", toNick));
    }

    public synchronized void broadcastClientsList() {
        StringBuilder sb = new StringBuilder("/clients ");
        for (ClientHandler handler : clients) {
            sb.append(handler.getName()).append(" ");
        }
        broadcastMsg(sb.toString());
    }

    public synchronized void unsubscribe(ClientHandler handler) {
        clients.remove(handler);
        broadcastClientsList();
    }

    public synchronized void subscribe(ClientHandler handler) {
        clients.add(handler);
        broadcastClientsList();
    }
}
