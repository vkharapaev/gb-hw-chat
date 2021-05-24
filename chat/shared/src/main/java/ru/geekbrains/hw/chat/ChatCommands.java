package ru.geekbrains.hw.chat;

public class ChatCommands {
    public static final String SERVER_AUTH_OK = "/authok";
    public static final String SERVER_CLIENTS = "/clients";
    public static final String CLIENT_AUTH = "/auth";
    public static final String CLIENT_TEMPLATE_AUTH = CLIENT_AUTH + " %s %s";
    public static final String CLIENT_REG = "/reg";
    public static final String CLIENT_TEMPLATE_REG = CLIENT_REG + " %s %s %s";
    public static final String CLIENT_END = "/end";
    public static final String CLIENT_PRIVATE_MSG = "/w";
    public static final String CLIENT_CHANGE_NICK = "/cn";
}
