package ru.geekbrains.hw.chat.server;

public class ServerApp {

    private static final int DEFAULT_PORT = 8189;

    public static void main(String[] args) {
        int port = args.length != 1 ?
                DEFAULT_PORT : Integer.parseInt(args[0]);
        new MyServer(port);
    }
}
